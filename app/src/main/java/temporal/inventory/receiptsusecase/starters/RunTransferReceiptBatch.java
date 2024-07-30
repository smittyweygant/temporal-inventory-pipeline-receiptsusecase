package temporal.inventory.receiptsusecase.starters;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import temporal.inventory.receiptsusecase.TransferMessageWorkflow;

public class RunTransferReceiptBatch {
    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) throws Exception {

        // Create a gRPC stubs wrapper that talks to the local Docker instance of
        // Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        // WorkflowClient can be used to start, signal, query, cancel, and terminate
        // Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Current time in milliseconds
        long currentTime = System.currentTimeMillis();

        // Define the workflow unique id
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId("TransferMessageBatch-" + currentTime)
                .setTaskQueue("TRANSFER_RECEIPTS_TASK_QUEUE")
                .build();

        // Create the workflow client stub.
        TransferMessageWorkflow workflow = client.newWorkflowStub(TransferMessageWorkflow.class, options);

        // Start the workflow execution

        System.out.println("Started Message Processor Workflow");

        // System.out.println(filePath);
        // Read the file content into a string

        URL resource = RunTransferReceipt.class.getClassLoader().getResource("TransferEvents.json");
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + "TransferEvents.json");
        } else {
            // Convert URL to Path
            Path path = Paths.get(resource.toURI());

            String eventData = new String(Files.readAllBytes(path));
            WorkflowClient.start(workflow::processEvents, eventData);
        }

        // System.out.println("TransferReceiptsWorkflow completed");
        System.exit(0);
        // return null;
    }
}
