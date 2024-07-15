package temporal.inventory.receiptsusecase;

public class SaveStatusActivityImpl implements SaveStatusActivity{
    
   @Override
    public String savestatus(String status){
      
        boolean isAcknowledgementSaved  = true;
            
     if (!isAcknowledgementSaved) 
    { 
        throw new RuntimeException("ACKNOWLEDGEMENT status was not saved to the database");
     }


    return "Event Status saved to EventDB :" + status;

    }
}
