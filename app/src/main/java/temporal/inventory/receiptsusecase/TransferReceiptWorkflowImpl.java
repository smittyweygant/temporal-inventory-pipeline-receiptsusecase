
package temporal.inventory.receiptsusecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.Iterator;

public class TransferReceiptWorkflowImpl implements TransferReceiptWorkflow {

    private final EmbassyTransformDataActivity activities = Workflow.newActivityStub(
            EmbassyTransformDataActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(1))
                    .setRetryOptions(
                RetryOptions.newBuilder()
                    .setMaximumAttempts(4)
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build())
            .build();
    );

    @Override
    public void processEvents(String eventData) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(eventData);

            if (rootNode.isArray()) {
                Iterator<JsonNode> records = rootNode.elements();
                while (records.hasNext()) {
                    JsonNode record = records.next();
                    String eventType = record.path("header").path("eventType").asText();

                    if ("LOGICAL_MOVE".equals(eventType) || 
                        "LOGICAL_MOVE_ADJUST".equals(eventType) || 
                        "TRANSFER_RECEIPT".equals(eventType)) {
                        activities.processRecord(eventType);
                    } else {
                        System.out.println("Unsupported event type. Skipping record.");
                    }
                }
            } else {
                System.out.println("Input JSON is not an array. Ending workflow.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
