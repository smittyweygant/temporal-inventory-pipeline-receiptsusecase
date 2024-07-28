# Demo - Inventory Transfer Reciept Pipeline

This repo is to develop workflows for a retail usecase described here: (https://docs.google.com/document/d/1Nq_ZPsC2p7fxeQcueEgFCz3YIGVwRlxnizQOV37Sg6o/edit?usp=sharing) transfer_receipt flow description and challenges

## Configuration

Two options:
1. Run the server locally  [local Temporal Server](https://docs.temporal.io/cli#starting-the-temporal-server)  on localhost:7233.

2. Connect to Temporal Cloud, set the following environment variables, replacing them with your own Temporal Cloud credentials:

```bash
TEMPORAL_ADDRESS=testnamespace.sdvdw.tmprl.cloud:7233
TEMPORAL_NAMESPACE=testnamespace.sdvdw
TEMPORAL_CERT_PATH="/path/to/file.pem"
TEMPORAL_KEY_PATH="/path/to/file.key"
````

## Configure Search Attributes
```bash
temporal operator search-attribute create --name "TRANSFER_EVENT_TYPE" --type keyword
temporal operator search-attribute create --name "TRANSFER_EVENT_STATUS" --type keyword
temporal operator search-attribute create --name "CORRELATION_ID" --type keyword
````

## Workflows
### TransferMessageWorkflow
A Temporal Workflow with one activity...mocks consuming a transfer message and routes/starts TransferReceipt Workflows.

### TransferReceiptWorkflow
Temporal Workflow that orchestrates the underlying APIs, ultimately flushing to storage.

## Demo Script

### Run worker
```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.TransferReceiptsWorker --console=plain
```` 

### Run starter
```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.StartMessageProcessor --console=plain
````

### Process flow overview
The StartMessageProcessor starter will submit a workflow start request with a pointer to the ./TransferEvents.json payload file. This file contains a batch of transfer messages, so it will simulate the handling of a queue of messages. 

### Message Processor Workflow
 Invokes child WF's for each valid transfer request. 

Each 'transfer event' in the batch will be parsed. Valid event types will trigger async child workflow starts. Invalid event types will throw an error visible in the Temporal console. Message processor workflow will await responses to each child workflow before noting completion. This demonstrates the notion of a fanout with logic to determine workflow actions. 

## Transfer Receipt Workflow
The Transfer Receipt workflow completes activities for transfer validation, enrichment, and state management update steps.

# View Workflow Status in the console
Customize the Temporal Console to show the 3 Custom Search Attributes. These may be used to filter / sort on the event types, event statuses, and correlation IDs.  

Move the CSA's and WF Duration columns so these can be seen in the console view
- Status
- Workflow ID
- TRANSFER_EVENT_TYPE
- TRANSFER_STATUS
- CORRELATION_ID

Click into the message processor WF. The workflow should still be open while waiting on the child workflows to complete. 

Click into one of the Transfer workflows. Review the timeline and Compact views to show the business state that the process is in. 