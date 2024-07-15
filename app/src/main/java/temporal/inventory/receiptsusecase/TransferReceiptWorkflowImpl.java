
package temporal.inventory.receiptsusecase;
import java.time.Duration;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Workflow;

public class TransferReceiptWorkflowImpl implements TransferReceiptWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(TransferReceiptWorkflowImpl.class);
    private final EmbassyTransformValidateDataActivity tvactivities = Workflow.newActivityStub(
        EmbassyTransformValidateDataActivity.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(10)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build()
            )
            .build()
    );

    private final GEOValidateDataActivity evalactivities = Workflow.newActivityStub(
        GEOValidateDataActivity.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(10)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build()
            )
            .build()
    );


    private final GEOTransformModelActivity geotransformactivities = Workflow.newActivityStub(
        GEOTransformModelActivity.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(14)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build()
            )
            .build()
    );

    private final SaveStatusActivity ssactivities = Workflow.newActivityStub(
        SaveStatusActivity.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(14)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build()
            )
            .build()
    );
    private final GEOEnrichmentActivity enrichactivities = Workflow.newActivityStub(
        GEOEnrichmentActivity.class,
       ActivityOptions.newBuilder()
           .setStartToCloseTimeout(Duration.ofMinutes(1))
           .setRetryOptions(RetryOptions.newBuilder()
                   .setMaximumAttempts(4)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                   .build()
          )
          .build()
    );

    private final PublishActivity publishactivities = Workflow.newActivityStub(
        PublishActivity.class,
       ActivityOptions.newBuilder()
           .setStartToCloseTimeout(Duration.ofMinutes(1))
           .setRetryOptions(RetryOptions.newBuilder()
                   .setMaximumAttempts(14)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                   .build()
          )
          .build()
    );
    private final  EmbassyAcknowledgeDataActivity ackactivities = Workflow.newActivityStub(
        EmbassyAcknowledgeDataActivity.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(1))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(14)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build()
            )
            .build()
    );
   
    

    @Override
    public void processEvents(String eventData) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

           
            ackactivities.ackEvents(eventData);
            String status="ACKNOWLEDGEMENT";
            ssactivities.savestatus(status);

            JsonNode rootNode = objectMapper.readTree(eventData);

            if (rootNode.isArray()) {
                Iterator<JsonNode> records = rootNode.elements();
                while (records.hasNext()) {
                    JsonNode record = records.next();
                    String eventType = record.path("header").path("eventType").asText();
                       
                    if ("LOGICAL_MOVE".equals(eventType) || 
                        "LOGICAL_MOVE_ADJUST".equals(eventType) || 
                        "TRANSFER_RECEIPT".equals(eventType)) {
                           // Workflow.sleep(Duration.ofSeconds(30));
                            tvactivities.processRecord(eventType);
                            
                           enrichactivities.enrichData(record);

                           String statusenriched="ENRICHMENT";

                           ssactivities.savestatus(statusenriched);

                           String statusvalidation="VALIDATION";
                           evalactivities.validateEvents();
                           ssactivities.savestatus(statusvalidation);

                           String statustranformationgeo="TRANFORMATION";
                           geotransformactivities.tranformtoeventmodel();
                            ssactivities.savestatus(statustranformationgeo);
                           

                           String statuspublish="PUBLISHED";
                            publishactivities.publishEvents();
                            ssactivities.savestatus(statuspublish);


                    } else {
                             Async.procedure(() -> tvactivities.rejectRecord(eventType));
                    //tvactivities.rejectRecord(eventType);
                    }
                }
            } else {
                System.out.println("Input JSON is not an array. Ending workflow.");
            }
        }catch (JsonProcessingException ex) {
            // Handle JSON processing exceptions
            logger.error("Failed to process JSON: {}", ex.getMessage(), ex);
            // Provide user-friendly feedback or rethrow a custom exception
            throw new CustomJsonProcessingException("An error occurred while processing the JSON data", ex);
        } //catch (RuntimeException ex) {
            // Handle other runtime exceptions
            //logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);
            // Provide user-friendly feedback or rethrow a custom exception
            //throw new CustomJsonProcessingException("A runtime error occurred while processing the event data", ex);
        //}
    }


class CustomJsonProcessingException extends RuntimeException {
    public CustomJsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
}