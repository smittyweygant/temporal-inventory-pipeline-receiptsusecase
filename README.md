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
The RunTransferReceiptBatch starter will submit a workflow start request with a pointer to the ./TransferEvents.json payload file. This file contains a batch of transfer messages, so it will simulate the handling of a queue of messages. 

### Message Processor Workflow
 Invokes child WF's for each valid transfer request. 

Each 'transfer event' in the batch will be parsed. Valid event types will trigger async child workflow starts. Invalid event types will throw an error visible in the Temporal console. Message processor workflow will await responses to each child workflow before noting completion. This demonstrates the notion of a fanout with logic to determine workflow actions. 

### Transfer Receipt Workflow
The Transfer Receipt workflow completes activities for transfer validation, enrichment, and state management update steps.

## View Workflow Status in the console
Customize the Temporal Console to show the 3 Custom Search Attributes. These may be used to filter / sort on the event types, event statuses, and correlation IDs.  

Move the CSA's and WF Duration columns so these can be seen in the console view
- Status
- Workflow ID
- TRANSFER_EVENT_TYPE
- TRANSFER_STATUS
- CORRELATION_ID

Click into the message processor WF. The workflow should still be open while waiting on the child workflows to complete. 

Click into one of the Transfer workflows. Review the timeline and Compact views to show the business state that the process is in. 

### Receipts Pipeline in Code as a Workflow + API Delay saved by Durable Execution 

```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.starters.RunTransferReceipt
````

Concepts 
Timeline view 
- Steps represent the actual function call executions handled by Temporal (UI --> code)
- Individual steps may execute an unreliable service, and Temporal handles this automatically
- API delay (timeout before responding)
- Detailed Status of specific steps (retries, what is currently pending, payload data, which help determine whether action is needed) 
- Payload data is visible - show input and output data for the workflow. Gives a foundation for end to end testability given known inputs / outputs.  

### Worker Crash
```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.starters.RunTransferReceipt
````
- Workflow has sleeps in it to allow us to see an issue in flight
- Show workflow starting to make progress
- Crash worker
- Back to UI - workflow state is maintained, waiting for a worker to pick up next step  
- Start worker - workflow finishes

### Now lets look at troubleshooting across a number of transfer requests 
```bash
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.starters.RunTransferReceiptBatch
````

- Show the console, workflows progressing steadily
- Visibility attributes that can be filtered
- Event type
- Correlation ID - ticket opened noting that something happened with a specific correlation ID
- Bunch of workflows are all open with the same correlation ID. Let's troubleshoot

### Address an issue with failing validation of message by an event type that should be working - bad event type Activity is caught by Message Processor before starting child WF. Fix is to update the external system that validates the event type

- Show message processor WF with pending activities (validation failed)
- Isolate the bug in the activity
- Fix, redeploy, WF's continue 

### Address issue rooted to a common correlation ID
- 





### Testing 
- Test the Receipt Processor workflow - given input, validate expected output 
- Replay production workflow in a test scenario

### For WFs - decide whether to fire as async 

