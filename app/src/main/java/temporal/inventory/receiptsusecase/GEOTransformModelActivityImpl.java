
package temporal.inventory.receiptsusecase;

import org.springframework.stereotype.Component;

@Component("geo-transform-model-activity")
public class GEOTransformModelActivityImpl implements GEOTransformModelActivity {
    @Override
    public String tranformtoeventmodel(){
     
        return "Inventory DataModel transfromed to Event Model:";

    }
}
