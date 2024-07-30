
package temporal.inventory.receiptsusecase;

import java.time.Duration;
import java.util.Iterator;

import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.common.RetryOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;

import java.util.ArrayList;
import java.util.List;

public class TransferMessageWorkflowImpl implements TransferMessageWorkflow {

    // set up logger
    private static final Logger log = LoggerFactory.getLogger(TransferMessageWorkflowImpl.class);

    // transfer state stored in a local object and status pushed to Temporal Advanced Visibility
    static final SearchAttributeKey<String> TRANSFER_EVENT_STATUS = SearchAttributeKey.forKeyword("TRANSFER_EVENT_STATUS");

    // activity retry policy
    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(3))
                    .setMaximumInterval(Duration.ofSeconds(15))
                    .setDoNotRetry(IllegalArgumentException.class.getName())
                    .build())
            .build();

    // Activity stubs
    private final Activities activities = Workflow.newActivityStub(
            Activities.class, options);

    @Override
    public void processEventsBatch(JsonNode events) {

        activities.ackEvents(events);
        String status = "ACKNOWLEDGEMENT";
        activities.saveStatus(status);

        if (events.isArray()) {

            // Iterate through batch of messages
            // Validate each message by type
            // Route valid messages to a child workflow
            // Invalid messages are either retried or ignored depending on type

            Iterator<JsonNode> eventIterator = events.elements();
            List<Promise<String>> promises = new ArrayList<>();

            while (eventIterator.hasNext()) {
                JsonNode event = eventIterator.next();
                String eventType = event.path("header").path("eventType").asText();
                String requestId = event.path("header").path("id").asText();
                String correlationId = event.path("header").path("correlationId").asText();

                // Filter the Event Type
                String[] response = activities.filterEventType(eventType);
                String responseCode = response[0];

                // If event type is valid, start a child workflow
                // to process transfer event activities
                if ("TRANSFER_EVENT".equals(responseCode)) {
                    ChildWorkflowOptions childWorkflowOptions =
                            ChildWorkflowOptions.newBuilder()
                                    .setWorkflowId("Transfer-Receipt-" + requestId)
                                    .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                                    .build();

                    TransferReceiptWorkflow receiptChildWorkflow = Workflow.newChildWorkflowStub(
                            TransferReceiptWorkflow.class, childWorkflowOptions);

                    // Start the child workflow asynchronously
                    Promise<String> receiptChildWorkflowPromise = Async.function(receiptChildWorkflow::processEvent, event);
                    promises.add(receiptChildWorkflowPromise);

                    System.out.println("Started Child Workflow to process transfer receipt");
                }
            }

            // Wait for all child workflows to complete
            Promise.allOf(promises).get();

        } else {
            System.out.println("Input JSON is not an array. Ending workflow.");
        }
    }

}