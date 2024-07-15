package temporal.inventory.receiptsusecase;

public class SaveStatusActivityImpl implements SaveStatusActivity{
    
   @Override
    public String savestatus(String status){
      
        boolean isstatusSaved  = true;
            
     if (!isstatusSaved) 
    { 
        throw new RuntimeException(status +"status was not saved to the database");
     }

    return "Event Status saved to EventDB :" + status;

    }
}
