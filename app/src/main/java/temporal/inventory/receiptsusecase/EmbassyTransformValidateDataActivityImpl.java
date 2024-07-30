package temporal.inventory.receiptsusecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.temporal.failure.ApplicationFailure;
import org.springframework.stereotype.Component;

@Component("embassy-transform-validate-data-activity")
public class EmbassyTransformValidateDataActivityImpl implements EmbassyTransformValidateDataActivity {
    private static final Logger logger = LoggerFactory.getLogger(EmbassyTransformValidateDataActivityImpl.class);

    @Override
    public String processRecord(String eventType) {
        System.out.println("Processing event type: " + eventType);
        //sleep(2);
        System.out.println("Routing the event to the GEO");
       return "Processing event type: " + eventType;
    }

           
    @Override
    public String validateRecord(String eventType) {
        // Workflow.sleep(Duration.ofSeconds(3));
        String responseType = "UNDETERMINED";
        switch (eventType) {
            case "LOGICAL_MOVE_ADJUST":
                // Change flag to trigger or suppress error
                boolean errorFlag = false;
                if (errorFlag) {
                    throw new Error("LOGICAL_MOVE_ADJUST is not a valid event type. Check data.");
                }
                responseType = "TRANSFER_EVENT";
                break;

            // Valid response types: 
            case "LOGICAL_MOVE":
            case "TRANSFER_RECEIPT":
            responseType = "TRANSFER_EVENT";
                break;
            default:
                responseType = "NON_TRANSFER";
                break;
        }
        return responseType;
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
}
