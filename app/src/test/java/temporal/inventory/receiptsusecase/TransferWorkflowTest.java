package temporal.inventory.receiptsusecase;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.api.enums.v1.IndexedValueType;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import temporal.inventory.receiptsusecase.starters.RunTransferReceipt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransferWorkflowTest {

    @Rule
    public TestWorkflowRule testWorkflowRule = TestWorkflowRule.newBuilder()
            .setWorkflowTypes(TransferReceiptWorkflowImpl.class)
            .registerSearchAttribute("TRANSFER_EVENT_STATUS", IndexedValueType.INDEXED_VALUE_TYPE_KEYWORD)
            .registerSearchAttribute("TRANSFER_EVENT_TYPE", IndexedValueType.INDEXED_VALUE_TYPE_KEYWORD)
            .registerSearchAttribute("CORRELATION_ID", IndexedValueType.INDEXED_VALUE_TYPE_KEYWORD)
            .setDoNotStart(true)
            .build();

    @Test
    public void testWorkflowHappyPath() throws IOException, URISyntaxException {
        testWorkflowRule.getWorker().registerActivitiesImplementations(new ActivitiesImpl());
        testWorkflowRule.getTestEnvironment().start();

        TransferReceiptWorkflow workflow = testWorkflowRule.getWorkflowClient()
                .newWorkflowStub(TransferReceiptWorkflow.class,
                        WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build());

        URL resource = RunTransferReceipt.class.getClassLoader().getResource("Transfer-single-event.json");
        if (resource == null) {
            throw new IOException("Resource not found: Transfer-single-event.json");
        }

        Path path = Paths.get(resource.toURI());
        String eventData = Files.readString(path);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode event = objectMapper.readTree(eventData);

        String result = workflow.processEvent(event);

        assertEquals("Receipt Event Processed", result);
    }

    @After
    public void tearDown() {
        testWorkflowRule.getTestEnvironment().shutdown();
    }
}
