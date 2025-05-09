package temporal.inventory.receiptsusecase.starters;

import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.serviceclient.WorkflowServiceStubs;
import temporal.inventory.receiptsusecase.TransferReceiptWorkflow;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class RunTransferReceipt {
    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) throws Exception {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Define the workflow unique id
        WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setWorkflowId("TransferReceipt-Example")
                        .setTaskQueue("TRANSFER_RECEIPTS_TASK_QUEUE")
                        .build();
        // Create the workflow client stub.
        TransferReceiptWorkflow workflow = client.newWorkflowStub(TransferReceiptWorkflow.class, options);

        // Start the workflow execution

        System.out.println("Executing TransferReceiptWorkflow");

        URL resource = RunTransferReceipt.class.getClassLoader().getResource("Transfer-single-event.json");
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + "Transfer-single-event.json");
        } else {
            // Convert URL to Path
            Path path = Paths.get(resource.toURI());

            try {
                String eventData = new String(Files.readAllBytes(path));
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode record = objectMapper.readTree(eventData);
                WorkflowClient.start(workflow::processEvent, record);
            } catch(JsonProcessingException ex){
                // Handle JSON processing exceptions
                System.out.println("Failed to process JSON: " + ex.getMessage());
                // Provide user-friendly feedback or rethrow a custom exception
                throw ApplicationFailure.newFailure("An error occurred while processing the JSON data", ex.getMessage(), ex);
            }

        }

        // System.out.println("TransferReceiptsWorkflow completed");
        System.exit(0);
        //return null;
    }
}
