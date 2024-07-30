package temporal.inventory.receiptsusecase;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.common.RetryOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

public class TransferMessageWorkflowImpl implements TransferMessageWorkflow {
    private static final Logger log = LoggerFactory.getLogger(TransferMessageWorkflowImpl.class);

    private static final SearchAttributeKey<String> TRANSFER_EVENT_STATUS = SearchAttributeKey.forKeyword("TRANSFER_EVENT_STATUS");

    private static final ActivityOptions ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(3))
                    .setMaximumInterval(Duration.ofSeconds(15))
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build())
            .build();

    private final Activities activities = Workflow.newActivityStub(Activities.class, ACTIVITY_OPTIONS);

    @Override
    public void processEventsBatch(JsonNode events) {
        log.info("Acknowledging events");
        activities.ackEvents(events);

        if (events.isArray()) {
            List<Promise<String>> promises = new ArrayList<>();
            Iterator<JsonNode> eventIterator = events.elements();

            while (eventIterator.hasNext()) {
                JsonNode event = eventIterator.next();
                if (isTransferEvent(event)) {
                    promises.add(startChildWorkflow(event));
                    log.info("Started Child Workflow to process transfer receipt");
                }
            }

            Promise.allOf(promises).get();
        } else {
            log.warn("Input JSON is not an array. Ending workflow.");
        }

    }

    private boolean isTransferEvent(JsonNode event) {
        String eventType = event.path("header").path("eventType").asText();
        String[] response = activities.filterEventType(eventType);
        return "TRANSFER_EVENT".equals(response[0]);
    }

    private Promise<String> startChildWorkflow(JsonNode event) {
        String requestId = event.path("header").path("id").asText();

        ChildWorkflowOptions childWorkflowOptions = ChildWorkflowOptions.newBuilder()
                .setWorkflowId("Transfer-Receipt-" + requestId)
                .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                .build();

        TransferReceiptWorkflow receiptChildWorkflow = Workflow.newChildWorkflowStub(TransferReceiptWorkflow.class, childWorkflowOptions);
        return Async.function(receiptChildWorkflow::processEvent, event);
    }
}
