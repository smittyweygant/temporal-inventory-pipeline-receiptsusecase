package temporal.inventory.nordstorm;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) throws Exception {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId("greeting-workflow")
                .setTaskQueue("greeting-tasks")
                .build();

        TransferReceiptWorkflow transferrecieptWorkflow = client.newWorkflowStub(GreetingWorkflow.class, options);

       String workflowId = WorkflowStub.fromTyped(transferrecieptWorkflow.getExecution().getWorkflowId();

      
        System.exit(0);
    }
}
