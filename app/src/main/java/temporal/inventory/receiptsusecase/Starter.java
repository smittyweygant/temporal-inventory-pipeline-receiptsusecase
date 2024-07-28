package temporal.inventory.receiptsusecase;
import java.io.IOException;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    @SuppressWarnings("CatchAndPrintStackTrace")
  public static void main(String[] args) throws Exception { 
   // runWorkflow();
    //System.exit(0);
  //}
  //public static String runWorkflow() throws FileNotFoundException, SSLException {
    // generate a random reference number

    // Workflow execution code
// Create a gRPC stubs wrapper that talks to the local Docker instance of Temporal service.
WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

// WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
WorkflowClient client = WorkflowClient.newInstance(service);

// Define the workflow unique id
    WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setWorkflowId("TransferReceipt")
            .setTaskQueue("TRANSFER_RECEIPTS_TASK_QUEUE")
            .build();
    // Create the workflow client stub.
    TransferReceiptWorkflow workflow = client.newWorkflowStub(TransferReceiptWorkflow.class, options);
   
    // Start the workflow execution

       System.out.println("Executing TransferReceiptWorkflow");
          

       String filePath = "./TransferEvents.json";

       // Read the file content into a string
      
      
       try {
        String eventData = FileUtils.readFileAsString(filePath);
        // Process eventData
       // workflow.processEvents(eventData);
       //WorkflowClient.start(workflow::ackEvents,eventData);
       WorkflowClient.start(workflow::processEvents,eventData);

    } catch (IOException e) {
    // Handle the exception, e.g., log it or rethrow it

    }
       // System.out.println("TransferReceiptsWorkflow completed");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
        System.exit(0);
        //return null;
}  
}
