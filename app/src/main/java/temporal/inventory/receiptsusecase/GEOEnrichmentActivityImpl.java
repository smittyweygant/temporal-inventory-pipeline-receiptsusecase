
package temporal.inventory.receiptsusecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GEOEnrichmentActivityImpl implements GEOEnrichmentActivity {

    private static final Logger logger = LoggerFactory.getLogger(GEOEnrichmentActivityImpl.class);

    @Override
    public JsonNode enrichData(JsonNode record) {

        try {
            // Implement your processing logic here

            System.out.println("GEO Enriching the record with Location and Item :");
            sleep(5);
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

    private void sleep(int seconds) {
        try {
            // a random number between 800 and 1200
            // to simulate variance in API call time
            long sleepTime = (long) (Math.random() * 400) + 800;

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
