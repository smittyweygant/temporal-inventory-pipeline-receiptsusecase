package temporal.inventory.receiptsusecase;


public class EmbassyTransformDataActivityImpl implements EmbassyTransformDataActivity {
    @Override
    public void processRecord(String eventType) {
        // Implement your processing logic here
        System.out.println("Processing event type: " + eventType);
        System.out.println("Routing the event to the GEO");
    }
    @Override
    public void rejectRecord(String eventType) {
        // Implement your processing logic here
        System.out.println("Processing event type: " + eventType);
        System.out.println("Skipping record," +"Unsupported event type." + eventType);

    }
}
