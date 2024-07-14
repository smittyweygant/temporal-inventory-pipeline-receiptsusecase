package temporal.inventory.receiptsusecase;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbassyTransformDataActivityImpl implements EmbassyTransformDataActivity {
    @Override
    public void processRecord(String eventType) {
        // Implement your processing logic here
        System.out.println("Processing event type: " + eventType);
        System.out.println("Routing the event to the GEO");
    }
}
