# Demo - Inventory Transfer Reciept Pipeline

This repo is to develop workflows for a retail usecase described here: (https://docs.google.com/document/d/1Nq_ZPsC2p7fxeQcueEgFCz3YIGVwRlxnizQOV37Sg6o/edit?usp=sharing) transfer_receipt flow description and challenges

## Configuration

Run the server locally  [local Temporal Server](https://docs.temporal.io/cli#starting-the-temporal-server)  on localhost:7233.

## Configure Search Attributes
```bash
temporal operator search-attribute create --name "TRANSFER_EVENT_TYPE" --type keyword
temporal operator search-attribute create --name "TRANSFER_EVENT_STATUS" --type keyword
temporal operator search-attribute create --name "CORRELATION_ID" --type keyword
````

Alternatively, run `create-search-attributes-localserver.sh`

## Workflows
### TransferMessageWorkflow
A Temporal Workflow with one activity...mocks consuming a transfer message and routes/starts TransferReceipt Workflows.

### TransferReceiptWorkflow
Temporal Workflow that orchestrates the underlying APIs, ultimately flushing to storage.

## Demo Script

### Run worker
```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.TransferReceiptsWorker
```` 

### Run starter
```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.starters.RunTransferReceiptBatch
````

## Process flow overview
Replaced with (internal) doc here: [Transfer Receipts Flow](https://docs.google.com/document/d/1YJLXcIXxcKir8bsI4csDj9kk0dS9Fut04Ik2x21lVEk/edit)

### Testing (TODO) 
Run an end-to-end TransferReceipt workflow test using

```bash
./gradlew test --info --tests "temporal.inventory.receiptsusecase.TransferWorkflowTest" 
```


- Test the Receipt Processor workflow - given input, validate expected output 
- Replay production workflow in a test scenario

