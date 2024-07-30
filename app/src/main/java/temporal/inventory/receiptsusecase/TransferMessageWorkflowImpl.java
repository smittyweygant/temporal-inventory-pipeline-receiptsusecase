
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

                                // Iterate through batch of messages
                                //  Validate each message by type
                                // Route valid messages to a child workflow 
                                // Invalid messages are either retried or ignored depending on type

                                Iterator<JsonNode> records = rootNode.elements();
                                while (records.hasNext()) {
                                        JsonNode record = records.next();
                                        String eventType = record.path("header").path("eventType").asText();
                                        String requestId = record.path("header").path("id").asText();
                                        String correlationId = record.path("header").path("correlationId").asText();

                                        // Validate the Event Type
                                        // Async.procedure(() -> tvactivities.validateRecord(eventType));
                                        String response = tvactivities.validateRecord(eventType);
                                        
                                        // If event type is valid, start a child workflow 
                                        // to process transfer event activities
                                        if ("TRANSFER_EVENT".equals(response)) {
                                                ChildWorkflowOptions childWorkflowOptions =
                                                        ChildWorkflowOptions.newBuilder()
                                                        .setWorkflowId("Transfer-Receipt-" + requestId)
                                                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                                                        .build();
                                                TransferReceiptWorkflow receiptChildWorkflow = Workflow.newChildWorkflowStub(TransferReceiptWorkflow.class, childWorkflowOptions);
                                                System.out.println("Start Child Workflow to process Receipt Event");
                                                receiptChildWorkflow.processEvents(record.toString());
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