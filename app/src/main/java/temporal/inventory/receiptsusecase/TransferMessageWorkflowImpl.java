
package temporal.inventory.receiptsusecase;

import java.time.Duration;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.common.RetryOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;

public class TransferMessageWorkflowImpl implements TransferMessageWorkflow {
        
        // set up logger
        private static final Logger log = LoggerFactory.getLogger(TransferMessageWorkflowImpl.class);
  
        // transfer state stored in a local object and status pushed to Temporal Advanced Visibility
        static final SearchAttributeKey<String> TRANSFER_EVENT_STATUS = SearchAttributeKey.forKeyword("TRANSFER_EVENT_STATUS");
        
        // activity retry policy
        private final ActivityOptions options = ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(30))
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(10)
                        .setDoNotRetry(IllegalArgumentException.class.getName())
                        .build())
                .build();
        
        
        // Activity stubs

        private final EmbassyAcknowledgeDataActivity ackactivities = Workflow.newActivityStub(
                        EmbassyAcknowledgeDataActivity.class, options);

        private final EmbassyTransformValidateDataActivity tvactivities = Workflow.newActivityStub(
                EmbassyTransformValidateDataActivity.class, options);

        private final SaveStatusActivity ssactivities = Workflow.newActivityStub(
                SaveStatusActivity.class, options);

        @Override
        public void processEvents(String eventData) {
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                        ackactivities.ackEvents(eventData);
                        String status = "ACKNOWLEDGEMENT";
                        ssactivities.savestatus(status);

                        JsonNode rootNode = objectMapper.readTree(eventData);

                        if (rootNode.isArray()) {
                                Iterator<JsonNode> records = rootNode.elements();
                                while (records.hasNext()) {
                                        JsonNode record = records.next();
                                        String eventType = record.path("header").path("eventType").asText();
                                        String requestId = record.path("header").path("id").asText();
                                        String correlationId = record.path("header").path("correlationId").asText();

                                        if ("LOGICAL_MOVE".equals(eventType) ||
                                                        "LOGICAL_MOVE_ADJUST".equals(eventType) ||
                                                        "TRANSFER_RECEIPT".equals(eventType)) {
                                                // Workflow.sleep(Duration.ofSeconds(30));

                                                // Start a child workflow to process transfer event activities
                                                ChildWorkflowOptions childWorkflowOptions =
                                                        ChildWorkflowOptions.newBuilder()
                                                        .setWorkflowId("Transfer-Receipt-" + requestId)
                                                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                                                        .build();
                                                TransferReceiptWorkflow receiptChildWorkflow = Workflow.newChildWorkflowStub(TransferReceiptWorkflow.class, childWorkflowOptions);
                                                
                                                System.out.println("Processing Receipt Event");
                                                receiptChildWorkflow.processEvents(record.toString());

                                        } else {
                                                Async.procedure(() -> tvactivities.rejectRecord(eventType));
                                                // tvactivities.rejectRecord(eventType);
                                        }
                                }
                        } else {
                                System.out.println("Input JSON is not an array. Ending workflow.");
                        }
                } catch (JsonProcessingException ex) {
                        // Handle JSON processing exceptions
                        log.error("Failed to process JSON: {}", ex.getMessage(), ex);
                        // Provide user-friendly feedback or rethrow a custom exception
                        throw new CustomJsonProcessingException("An error occurred while processing the JSON data", ex);
                } 
                // Handle other runtime exceptions
                catch (RuntimeException ex) {
                log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
                }
        }

        class CustomJsonProcessingException extends RuntimeException {
                public CustomJsonProcessingException(String message, Throwable cause) {
                        super(message, cause);
                }
        }
}