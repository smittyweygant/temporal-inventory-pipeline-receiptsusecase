package temporal.inventory.receiptsusecase;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

    
@ActivityInterface
    
public interface EmbassyTransformDataActivity {
    @ActivityMethod
    void processRecord(String eventType);
}
  
