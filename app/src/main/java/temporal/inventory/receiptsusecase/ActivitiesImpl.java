
package temporal.inventory.receiptsusecase;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
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


        logger.info("GEO: Enriching the record with Location and Item ");
        simulateDelayRandom(1);
        ((ObjectNode) record).put("location", "New York");
        ((ObjectNode) record).put("item", "DiorBag");

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return record;
    }

    @Override
    public String processRecord(String eventType) {
        logger.info("Processing event type: " + eventType);
        //sleep(2);
        logger.info("Routing the event to Geo");
        return "Processing event type: " + eventType;
    }


    @Override
    public String[] filterEventType(String eventType) {
        // Workflow.sleep(Duration.ofSeconds(3));
        String[] responseType;
        switch (eventType) {
            // Valid response types:
            case "LOGICAL_MOVE_ADJUST":
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
    public String ackEvents(JsonNode eventData) {

        if (eventData != null && !eventData.isEmpty()) {
            // The events are not empty
            logger.info("Event consumed from Kafka");
            return "Event consumed from Kafka";

        } else {
            // The string is empty or null
            logger.info("Event not consumed from Kafka");
            Exception e = new RuntimeException(" Intentionally failing the workflow due to no data consumed from kafka. ");
            throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.getClass().getName());
        }

    }

    @Override
    public String TransformToEventModel() {

        boolean simulateAPIDowntime = false;
        if (simulateAPIDowntime) {
            ActivityExecutionContext ctx = Activity.getExecutionContext();
            int activityAttempt = ctx.getInfo().getAttempt();

            if (activityAttempt < 4) {
                simulateDelayRandom(4);
                throw new RuntimeException("API call timed out after 5 seconds. Retry attempt: " + activityAttempt);
            } else {
                logger.info("API recovered");
            }

        }

        simulateDelayRandom(1);
        return "GEO: Inventory DataModel transformed to Event Model";

    }

    @Override
    public String validateEvents(JsonNode eventData) {
        simulateDelayRandom(1);

        String correlationId = eventData.path("header").path("correlationId").asText();
        // System.out.println("VALIDATING event data for correlationId: " + correlationId);

        boolean simulateBadDataBug = false;
        if (simulateBadDataBug && correlationId.equals("P0000000002018374594")) {
            // simulate API failure
            throw new RuntimeException("Invalid data found for event. Failing validation.");
        }

        return "GEO: Event Data Validated";

    }

    @Override
    public String saveStatus(String status) {

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
