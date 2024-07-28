package temporal.inventory.receiptsusecase;

import org.springframework.stereotype.Component;

@Component("geo-validate-data-activity")
public class GEOValidateDataActivityImpl implements GEOValidateDataActivity{
    
   @Override
    public String validateEvents(){
    sleep(1);
     return "Event Data Validated:";

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
