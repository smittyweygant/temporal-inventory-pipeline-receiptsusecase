package temporal.inventory.receiptsusecase;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface TransferReceiptWorkflow {
  @WorkflowMethod
  void processEvents(String eventData);
}
