
package temporal.inventory.receiptsusecase;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component("geo-enrichment-activity")
public class ActivitiesImpl implements Activities {

    private static final Logger logger = LoggerFactory.getLogger(Activities.class);

    @Override
    public JsonNode enrichData(JsonNode record) {

        try {
            System.out.println("GEO: Enriching the record with Location and Item ");
            simulateDelayRandom(1);
            ((ObjectNode) record).put("location", "New York");
            ((ObjectNode) record).put("item", "DiorBag");

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            System.out.println(mapper.writeValueAsString(record));

        } catch (JsonProcessingException ex) {

            // Log the exception
            logger.error("Failed to process JSON: {}", ex.getMessage(), ex);

            // Provide user-friendly feedback (for example, throw a custom exception)
            throw new CustomJsonProcessingException("An error occurred while processing the JSON data", ex);
        }
        return record;
    }

    @Override
    public String processRecord(String eventType) {
        System.out.println("Processing event type: " + eventType);
        //sleep(2);
        System.out.println("Routing the event to Geo");
        return "Processing event type: " + eventType;
    }


    @Override
    public String[] validateRecord(String eventType) {
        // Workflow.sleep(Duration.ofSeconds(3));
        String[] responseType;
        switch (eventType) {
            case "LOGICAL_MOVE_ADJUST":
                // Change flag to suppress error
                boolean errorFlag = false;

                if (errorFlag) {
                    throw new Error("Detected INVALID event type: LOGICAL_MOVE_ADJUST. Check data.");
                }
                responseType = new String[]{"TRANSFER_EVENT", "Processing transfer event type " + eventType};
                break;

            // Valid response types:
            case "LOGICAL_MOVE":
            case "TRANSFER_RECEIPT":
                responseType = new String[]{"TRANSFER_EVENT", "Processing transfer event type " + eventType};
                break;
            default:
                responseType = new String[]{"NON_TRANSFER", "Filtered out known non-transfer event type " + eventType +
                        " from processing."};
                break;
        }
        return responseType;
    }

    @Override
    public String ackEvents(String eventData) {

        if (eventData != null && !eventData.isEmpty()) {
            // The events are not empty
            System.out.println("Event consumed from Kafka");
            return "Event consumed from Kafka";

        } else {
            // The string is empty or null
            System.out.println("Event not consumed from Kafka");
            Exception e = new RuntimeException(" Intentionally failing the workflow due to no data consumed from kafka. ");
            throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.getClass().getName());
        }

    }

    @Override
    public String TransformToEventModel() {
        simulateDelayRandom(1);
        return "GEO: Inventory DataModel transformed to Event Model";

    }

    @Override
    public String validateEvents() {
        simulateDelayRandom(1);
        return "GEO: Event Data Validated";

    }

    @Override
    public String saveStatus(String status) {

        boolean isstatusSaved = true;

        if (!isstatusSaved) {
            throw new RuntimeException(status + "status was not saved to the database");
        }

        return "Event Status saved to EventDb: " + status;

    }

    @Override
    public String publishEvents() {
        simulateDelayRandom(1);
        return "Transfer Receipt data written to database";

    }

    private void simulateDelayRandom(int seconds) {
        try {
            // to simulate variance in API call time
            long sleepTime = (long) (Math.random() * 600) + 800;

            Thread.sleep(seconds * sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    class CustomJsonProcessingException extends RuntimeException {
        public CustomJsonProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
