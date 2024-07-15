package temporal.inventory.receiptsusecase;
import com.fasterxml.jackson.databind.JsonNode;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
    
@ActivityInterface
    
public interface GEOEnrichmentActivity{
    @ActivityMethod
    JsonNode enrichData(JsonNode record);
    
}
 