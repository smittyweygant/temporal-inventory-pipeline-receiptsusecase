package temporal.inventory.receiptsusecase;
import com.fasterxml.jackson.databind.JsonNode;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
    
@ActivityInterface
    
public interface Activities {
    @ActivityMethod
    JsonNode enrichData(JsonNode record);

    @ActivityMethod
    String processRecord(String eventType);

    @ActivityMethod
    String[] filterEventType(String eventType);

    @ActivityMethod
    String ackEvents(JsonNode eventData);

    @ActivityMethod
    String TransformToEventModel();

    @ActivityMethod
    String validateEvents(JsonNode eventData);

    @ActivityMethod
    String publishEvents();

    @ActivityMethod
    String saveStatus(String status);
}
