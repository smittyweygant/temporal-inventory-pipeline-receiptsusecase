package temporal.inventory.receiptsusecase;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.workflow.Workflow;

public class TransferReceiptWorkflowImpl implements TransferReceiptWorkflow {
    private static final Logger log = LoggerFactory.getLogger(TransferReceiptWorkflowImpl.class);

    private static final SearchAttributeKey<String> TRANSFER_EVENT_TYPE = SearchAttributeKey.forKeyword("TRANSFER_EVENT_TYPE");
    private static final SearchAttributeKey<String> TRANSFER_EVENT_STATUS = SearchAttributeKey.forKeyword("TRANSFER_EVENT_STATUS");
    private static final SearchAttributeKey<String> CORRELATION_ID = SearchAttributeKey.forKeyword("CORRELATION_ID");

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
    public String processEvent(JsonNode event) {
        log.info("Processing Receipt Event");

        updateStatus("ACKNOWLEDGEMENT");
        updateSearchAttributes(event);

        updateStatus("ENRICHMENT");
        activities.enrichData(event);

        updateStatus("VALIDATION");
        activities.validateEvents(event);

        updateStatus("TRANSFORMATION");
        activities.TransformToEventModel();

        updateStatus("PUBLISHED");
        activities.publishEvents();

        return "Receipt Event Processed";
    }

    private String updateStatus(String status) {
        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_STATUS.valueSet(status));
        activities.saveStatus(status);
        return status;
    }

    private void updateSearchAttributes(JsonNode event) {
        String eventType = event.path("header").path("eventType").asText();
        String correlationId = event.path("header").path("correlationId").asText();

        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_TYPE.valueSet(eventType));
        Workflow.upsertTypedSearchAttributes(CORRELATION_ID.valueSet(correlationId));
    }
}
