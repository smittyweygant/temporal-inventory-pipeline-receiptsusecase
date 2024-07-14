package temporal.inventory.receiptsusecase;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class Starter {
    public static void main(String[] args) {
        // Create a gRPC stubs wrapper that talks to the local Docker instance of Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Define the workflow unique id
        String workflowId = "TransferReceiptWorkflow";

        // Define our workflow options
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue("TRANSFER_RECEIPTS_TASK_QUEUE")
                .build();

        // Create the workflow client stub.
        TransferReceiptWorkflow workflow = client.newWorkflowStub(TransferReceiptWorkflow.class, options);
        
      
        
     // Create sample JSON data (array of records)
        String eventData = "["
                + "{ \"header\": { \"source\": { \"channelCountry\": \"CA\", \"channel\": \"ONLINE\", \"platform\": \"WEB\", \"feature\": \"DISCOUNT\", \"serviceName\": \"EXPRESS\", \"store\": \"STORE_001\", \"register\": \"REGISTER_01\" }, \"id\": \"b2e8d9f4-3b7c-4df6-9d3e-9d41c4d78f62\", \"correlationId\": \"P0000000002018374593\", \"eventTime\": \"2024-06-19T09:15:23.450Z\", \"eventType\": \"LOGICAL_MOVE\", \"sourceTime\": \"2024-06-19T09:10:00Z\", \"sourceSystem\": \"SAP\", \"fromLocation\": { \"facility\": \"123\", \"logical\": \"124\" }, \"toLocation\": { \"facility\": \"125\", \"logical\": \"126\" }, \"userId\": \"user_001\" }, \"inventoryAdjustment\": { \"adjustmentType\": \"INCREASE\", \"adjustmentReason\": \"STOCK_CORRECTION\", \"quantity\": 10 }, \"receipt\": null, \"shipment\": null, \"transfer\": null, \"rtv\": null, \"stockLedgerCorrection\": null },"
                + "{ \"header\": { \"source\": { \"channelCountry\": \"UK\", \"channel\": \"STORE\", \"platform\": \"POS\", \"feature\": \"SALE\", \"serviceName\": \"STANDARD\", \"store\": \"STORE_002\", \"register\": \"REGISTER_02\" }, \"id\": \"c3f7e8d4-5c8d-4f6e-9e4d-6f72c5f8d81f\", \"correlationId\": \"P0000000002018374594\", \"eventTime\": \"2024-06-20T11:25:33.560Z\", \"eventType\": \"LOGICAL_MOVE_ADJUST\", \"sourceTime\": \"2024-06-20T11:20:00Z\", \"sourceSystem\": \"OMS\", \"fromLocation\": { \"facility\": \"223\", \"logical\": \"224\" }, \"toLocation\": { \"facility\": \"225\", \"logical\": \"226\" }, \"userId\": \"user_002\" }, \"inventoryAdjustment\": { \"adjustmentType\": \"DECREASE\", \"adjustmentReason\": \"DAMAGE\", \"quantity\": 5 }, \"receipt\": null, \"shipment\": null, \"transfer\": null, \"rtv\": null, \"stockLedgerCorrection\": null },"
                + "{ \"header\": { \"source\": { \"channelCountry\": \"AU\", \"channel\": \"MOBILE\", \"platform\": \"APP\", \"feature\": \"RETURN\", \"serviceName\": \"PRIORITY\", \"store\": \"STORE_003\", \"register\": \"REGISTER_03\" }, \"id\": \"d4g8f9h4-6d9e-4g7e-9f5e-7g83d6f9e82g\", \"correlationId\": \"P0000000002018374595\", \"eventTime\": \"2024-06-21T13:35:43.670Z\", \"eventType\": \"TRANSFER_RECEIPT\", \"sourceTime\": \"2024-06-21T13:30:00Z\", \"sourceSystem\": \"WMS\", \"fromLocation\": null, \"toLocation\": { \"facility\": \"323\", \"logical\": \"324\" }, \"userId\": \"user_003\" }, \"inventoryAdjustment\": null, \"receipt\": { \"asnNumber\": \"9025941464\", \"rmsShipmentNumber\": \"SH12345\", \"supplier\": \"6157747\", \"directStoreDelivery\": \"YES\", \"receiptDetails\": [ { \"operationType\": \"TRANSFER\", \"initiatedBy\": \"OMS\", \"cartonNumber\": \"20241205693124120003\", \"receiptTime\": \"2024-06-21T13:30:00Z\", \"operationNumber\": \"9025941464\", \"externalReferenceNumber\": \"EXT12345\", \"allocationPurchaseOrderNumber\": \"PO12345\", \"cartonDetails\": [ { \"products\": { \"rmsSku\": { \"idType\": \"RMS\", \"id\": \"2634510\" }, \"externalSku\": \"EXTSKU12345\" }, \"quantity\": 2, \"fromDisposition\": \"NEW\", \"toDisposition\": \"AVAILABLE\", \"reasonCode\": \"TFRC\", \"unitCost\": 50.00 } ] } ] }, \"shipment\": null, \"transfer\": null, \"rtv\": null, \"stockLedgerCorrection\": null },"
                // Additional records can be added here following the same structure
                + "{ \"header\": { \"source\": { \"channelCountry\": \"US\", \"channel\": \"WEB\", \"platform\": \"MOBILE_APP\", \"feature\": \"PROMOTION\", \"serviceName\": \"STANDARD\", \"store\": \"STORE_004\", \"register\": \"REGISTER_04\" }, \"id\": \"e5h9i0j4-7h0i-5j8i-0j6i-8i94j0k1l92k\", \"correlationId\": \"P0000000002018374596\", \"eventTime\": \"2024-06-22T14:45:53.780Z\", \"eventType\": \"LOGICAL_MOVE\", \"sourceTime\": \"2024-06-22T14:40:00Z\", \"sourceSystem\": \"ERP\", \"fromLocation\": { \"facility\": \"423\", \"logical\": \"424\" }, \"toLocation\": { \"facility\": \"425\", \"logical\": \"426\" }, \"userId\": \"user_004\" }, \"inventoryAdjustment\": { \"adjustmentType\": \"INCREASE\", \"adjustmentReason\": \"REPLENISHMENT\", \"quantity\": 20 }, \"receipt\": null, \"shipment\": null, \"transfer\": null, \"rtv\": null, \"stockLedgerCorrection\": null },"
                + "{ \"header\": { \"source\": { \"channelCountry\": \"DE\", \"channel\": \"STORE\", \"platform\": \"POS\", \"feature\": \"SALE\", \"serviceName\": \"STANDARD\", \"store\": \"STORE_005\", \"register\": \"REGISTER_05\" }, \"id\": \"f6i0j1k4-8i1j-6k9j-1k7j-9j05k2m3n93l\", \"correlationId\": \"P0000000002018374597\", \"eventTime\": \"2024-06-23T15:55:03.890Z\", \"eventType\": \"LOGICAL_MOVE_ADJUST\", \"sourceTime\": \"2024-06-23T15:50:00Z\", \"sourceSystem\": \"OMS\", \"fromLocation\": { \"facility\": \"523\", \"logical\": \"524\" }, \"toLocation\": { \"facility\": \"525\", \"logical\": \"526\" }, \"userId\": \"user_005\" }, \"inventoryAdjustment\": { \"adjustmentType\": \"DECREASE\", \"adjustmentReason\": \"LOSS\", \"quantity\": 15 }, \"receipt\": null, \"shipment\": null, \"transfer\": null, \"rtv\": null, \"stockLedgerCorrection\": null },"
                + "{ \"header\": { \"source\": { \"channelCountry\": \"JP\", \"channel\": \"MOBILE\", \"platform\": \"APP\", \"feature\": \"RETURN\", \"serviceName\": \"PRIORITY\", \"store\": \"STORE_006\", \"register\": \"REGISTER_06\" }, \"id\": \"g7j1k2l4-9j2k-7l0k-2l8k-0k16l3m4o94m\", \"correlationId\": \"P0000000002018374598\", \"eventTime\": \"2024-06-24T16:05:14.000Z\", \"eventType\": \"TRANSFER_RECEIPT\", \"sourceTime\": \"2024-06-24T16:00:00Z\", \"sourceSystem\": \"WMS\", \"fromLocation\": null, \"toLocation\": { \"facility\": \"623\", \"logical\": \"624\" }, \"userId\": \"user_006\" }, \"inventoryAdjustment\": null, \"receipt\": { \"asnNumber\": \"9025941465\",
        // Start the workflow execution

       System.out.println("Executing TransferReceiptWorkflow");
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
        workflow.processEvents(eventData);
        
        System.exit(0);
    }
}
