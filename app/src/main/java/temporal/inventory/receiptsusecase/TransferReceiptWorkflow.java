package temporal.inventory.receiptsusecase;

import com.fasterxml.jackson.databind.JsonNode;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface TransferReceiptWorkflow {
  @WorkflowMethod
  String processEvent(JsonNode eventData);

}
