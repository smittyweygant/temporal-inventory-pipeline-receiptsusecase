package temporal.inventory.receiptsusecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import temporal.inventory.receiptsusecase.starters.RunTransferReceipt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootTest(
        classes = {
                TransferReceiptWorkflowImplTest.Configuration.class,
        })
// @ActiveProfiles("Inventory Pipeline Tests")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@EnableAutoConfiguration()
@DirtiesContext
public class TransferReceiptWorkflowImplTest {
    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Autowired
    TestWorkflowEnvironment testWorkflowEnvironment;

    @Autowired
    WorkflowClient workflowClient;

    @Value("${spring.temporal.workers[0].task-queue}")
    String taskQueue;

    @AfterEach
    public void after() {
        testWorkflowEnvironment.close();
    }

    @BeforeEach
    void beforeEach() {
        applicationContext.start();
    }

    @Test
    public void givenValidArgs_itShouldExecuteWorkflow() throws JsonProcessingException, URISyntaxException {
        // poor man's fixture
        URL resource = RunTransferReceipt.class.getClassLoader().getResource("TransferEvents.json");

        // Convert URL to Path
        Path path = Paths.get(resource.toURI());

        // Read the file content into a string
        String eventData = "";
        try {
            eventData = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            Assertions.fail(e);
        }
        String wid = UUID.randomUUID().toString();
        TransferReceiptWorkflow sut =
                workflowClient.newWorkflowStub(TransferReceiptWorkflow.class,
                        WorkflowOptions.newBuilder().setTaskQueue(taskQueue).setWorkflowId(wid).build());
        String finalEventData = eventData;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(eventData);
        Assertions.assertDoesNotThrow(() -> {
            WorkflowClient.start(sut::processEvent, rootNode);
        });
    }

    @ComponentScan
    public static class Configuration {

    }
}