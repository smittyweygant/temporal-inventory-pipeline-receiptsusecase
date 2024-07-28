package temporal.inventory.receiptsusecase;
import io.temporal.failure.ApplicationFailure;
import org.springframework.stereotype.Component;

@Component("embassy-acknowledge-data-activity")
public class EmbassyAcknowledgeDataActivityImpl implements EmbassyAcknowledgeDataActivity {

    @Override
    public String ackEvents(String eventData){
          
        if (eventData != null && !eventData.isEmpty()) {
            // The events are not empty
            System.out.println("Event consumed from Kafka");
            //WorkflowUtils.saveStatustoDB("ACKNOWLEDGEMENT");
            
            //if (!WorkflowUtils.isIsAcknowledgementSaved()) 
           // { 
               // throw new RuntimeException("ACKNOWLEDGEMENT status was not saved to the database");
           //  }

            sleep(2);
            return "Event consumed from Kafka";
            
        } else {
            // The string is empty or null
            System.out.println("Event not consumed from Kafka");
            Exception e = new RuntimeException(" Intentionally failing the workflow due to no data consumed from kafka. ");
       throw ApplicationFailure.newNonRetryableFailure(e.getMessage(), e.getClass().getName());
    }
            
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