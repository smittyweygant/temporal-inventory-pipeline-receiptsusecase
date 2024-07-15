package temporal.inventory.receiptsusecase;


import io.temporal.failure.ApplicationFailure;
public class EmbassyTransformDataActivityImpl implements EmbassyTransformDataActivity {
    // private static final Logger logger = LoggerFactory.getLogger(EmbassyTransformDataActivityImpl.class);

    @Override
    public String processRecord(String eventType) {
        // Implement your processing logic here
        System.out.println("Processing event type: " + eventType);
        //sleep(10);
        System.out.println("Routing the event to the GEO");

        return eventType;

    }
    @Override
    public void rejectRecord(String eventType) {
        // Implement your processing logic here
       
        //sleep(3);
        System.out.println("Skipping record," +"Unsupported event type." + eventType);
       // throw new RuntimeException("Intentionally failing the workflow due to mismatched eventType.");
       Exception e = new RuntimeException(" Intentionally failing the workflow due to mismatched eventType. ");
       throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.getClass().getName());
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
