
package temporal.inventory.receiptsusecase;

import java.time.Duration;

import io.temporal.failure.ApplicationFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.common.SearchAttributeKey;
import io.temporal.workflow.Workflow;

public class TransferReceiptWorkflowImpl implements TransferReceiptWorkflow {

    // set up logger
    private static final Logger log = LoggerFactory.getLogger(TransferReceiptWorkflowImpl.class);

    // transfer state stored in a local object and status pushed to Temporal Advanced Visibility
    static final SearchAttributeKey<String> TRANSFER_EVENT_TYPE = SearchAttributeKey.forKeyword("TRANSFER_EVENT_TYPE");
    static final SearchAttributeKey<String> TRANSFER_EVENT_STATUS = SearchAttributeKey.forKeyword("TRANSFER_EVENT_STATUS");
    static final SearchAttributeKey<String> CORRELATION_ID = SearchAttributeKey.forKeyword("CORRELATION_ID");

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
    public String processEvents(JsonNode record) {

        System.out.println("Processing Receipt Event");

        String status = "ACKNOWLEDGEMENT";
        activities.saveStatus(status);
        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_STATUS.valueSet(status));

        // Parse and process transfer record
        String eventType = record.path("header").path("eventType").asText();
        String correlationId = record.path("header").path("correlationId").asText();
        Workflow.upsertTypedSearchAttributes(CORRELATION_ID.valueSet(correlationId));

        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_TYPE.valueSet(eventType));

        status = "ENRICHMENT";
        activities.enrichData(record);
        activities.saveStatus(status);
        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_STATUS.valueSet(status));

        status = "VALIDATION";
        activities.validateEvents();
        activities.saveStatus(status);
        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_STATUS.valueSet(status));

        status = "TRANSFORMATION";
        activities.TransformToEventModel();
        activities.saveStatus(status);
        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_STATUS.valueSet(status));

        status = "PUBLISHED";
        activities.publishEvents();
        activities.saveStatus(status);
        Workflow.upsertTypedSearchAttributes(TRANSFER_EVENT_STATUS.valueSet(status));

        return status;
    }

}