package temporal.inventory.receiptsusecase;
public class WorkflowUtils {


    private static  boolean isAcknowledgementSaved =false;
    // Utility method to check if a string is not empty
    public static String saveStatustoDB(String status) {
        isAcknowledgementSaved = true;
        return "Event Status saved to EventDB :" + status;

    }
    // Add other utility methods as needed

    public static boolean isIsAcknowledgementSaved() {
        return isAcknowledgementSaved;
    }
}