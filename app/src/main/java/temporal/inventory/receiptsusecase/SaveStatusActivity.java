
package temporal.inventory.receiptsusecase;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SaveStatusActivity {
    @ActivityMethod
    String savestatus(String status);

}


