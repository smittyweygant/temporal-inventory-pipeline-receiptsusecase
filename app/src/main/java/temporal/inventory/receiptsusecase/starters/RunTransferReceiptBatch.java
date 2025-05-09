package temporal.inventory.receiptsusecase.starters;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
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

            try {
                String eventData = new String(Files.readAllBytes(path));
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(eventData);
                WorkflowClient.start(workflow::processEventsBatch, rootNode);
            } catch(JsonProcessingException ex){
                // Handle JSON processing exceptions
                System.out.println("Failed to process JSON: " + ex.getMessage());
                // Provide user-friendly feedback or rethrow a custom exception
                throw ApplicationFailure.newFailure("An error occurred while processing the JSON data", ex.getMessage(), ex);
            }



        }

        // System.out.println("TransferReceiptsWorkflow completed");
        System.exit(0);
        // return null;
    }
}
