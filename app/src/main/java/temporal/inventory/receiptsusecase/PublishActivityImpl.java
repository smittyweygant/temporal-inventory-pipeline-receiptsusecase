
package temporal.inventory.receiptsusecase;

public class PublishActivityImpl implements PublishActivity{
    
   @Override
    public String publishEvents(){
    
     return "TRANSFER RECEIPTS Event Data Published:";

    }
}
