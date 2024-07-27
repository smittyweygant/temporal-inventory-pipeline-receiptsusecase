package temporal.inventory.receiptsusecase;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface TransferMessageWorkflow {
  @WorkflowMethod
  void processEvents(String eventData);

}
