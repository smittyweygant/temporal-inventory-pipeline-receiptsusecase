package temporal.inventory.receiptsusecase;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

    
@ActivityInterface
    
public interface EmbassyTransformValidateDataActivity {
    @ActivityMethod
    String processRecord(String eventType);
    @ActivityMethod
    String validateRecord(String eventType);
}
  
