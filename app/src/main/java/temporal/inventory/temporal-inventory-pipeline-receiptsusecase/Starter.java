package temporal.inventory.receiptsusecase;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        // Create a gRPC stubs wrapper that talks to the local Docker instance of Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Define the workflow unique id
        String workflowId = "TransferReceiptWorkflow";

        // Define our workflow options
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue("TRANSFER_RECEIPTS_TASK_QUEUE")
                .build();

        // Create the workflow client stub.
        TransferReceiptWorkflow workflow = client.newWorkflowStub(TransferReceiptWorkflow.class, options);
        
     
    // Start the workflow execution

       System.out.println("Executing TransferReceiptsWorkflow");
          

       String filePath = "/Users/geethaanne/Desktop/Nordstrom/transfer_receipt_payload.txt";

       // Read the file content into a string
       String eventData = FileUtils.readFileAsString(filePath); 
       
       workflow.processEvents(eventData);

       System.out.println("TransferReceiptsWorkflow completed");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
        System.exit(0);
    }
}
