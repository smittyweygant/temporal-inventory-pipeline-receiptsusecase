package temporal.inventory.receiptsusecase;
import java.io.IOException;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
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
          

       String filePath = "/Users/geethaanne/Desktop/Nordstrom/temporal-inventory-pipeline-receiptsusecase/TransferEvents.json";

       // Read the file content into a string
      
      
       try {
        String eventData = FileUtils.readFileAsString(filePath);
        // Process eventData
        workflow.processEvents(eventData);
    } catch (IOException e) {
        e.printStackTrace();
        // Handle the exception, e.g., log it or rethrow it
    }
       
    
       System.out.println("TransferReceiptsWorkflow completed");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
        System.exit(0);
    }
}
