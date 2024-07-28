package temporal.inventory.receiptsusecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.temporal.failure.ApplicationFailure;

public class EmbassyTransformValidateDataActivityImpl implements EmbassyTransformValidateDataActivity {
    private static final Logger logger = LoggerFactory.getLogger(EmbassyTransformValidateDataActivityImpl.class);

    @Override
    public String processRecord(String eventType) {
        // Implement your processing logic here
       //try {
        System.out.println("Processing event type: " + eventType);
        //sleep(2);
        System.out.println("Routing the event to the GEO");

      //logger.info("\n\nSimulating Processing activity failure.\n\n");
      // throw new RuntimeException("Error causing the processing mismatched record");
    


       return "Processing event type: " + eventType;
    }
       //} catch (Exception e) {


        //logger.info("\n\nSimulating Processing activity failure.\n\n");
        // throw new RuntimeException("Error causing the processing mismatched record");
    
        

        // Simulate processing failure
      // logger.info("\n\nSimulating Processing activity failure.\n\n");
      // throw new RuntimeException("Error causing the processing API go down!");
           // Exception e = new RuntimeException("Processing the event failed");
           //throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.getClass().getName());
           
         //  logger.info("\n\nSimulating Processing activity failure.\n\n");
          // throw new RuntimeException("Error causing the processing mismacthed record");
           
          



    
    @Override
    public void rejectRecord(String eventType) {
        // Implement your processing logic here
       
       sleep(1);
        System.out.println("Skipping record," +"Unsupported event type." + eventType);
        //throw new RuntimeException("Intentionally failing the workflow due to mismatched eventType.");
       Exception e = new RuntimeException(" Intentionally failing the workflow due to mismatched eventType. ");
       throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.getClass().getName());

      // logger.info("\n\nSimulating Processing activity failure.\n\n");
      //throw new RuntimeException("Error causing the processing mismacthed record");

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
