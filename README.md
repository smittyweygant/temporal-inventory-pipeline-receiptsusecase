# Demo - Inventory Transfer Reciept Pipeline

This repo is to develop workflows for a retail usecase described here: (https://docs.google.com/document/d/1Nq_ZPsC2p7fxeQcueEgFCz3YIGVwRlxnizQOV37Sg6o/edit?usp=sharing) transfer_receipt flow description and challenges

# Configuration

## Running locally or Temporal Cloud:
1. Run the server locally  [local Temporal Server](https://docs.temporal.io/cli#starting-the-temporal-server)  on localhost:7233.

2. Connect to Temporal Cloud, set the following environment variables, replacing them with your own Temporal Cloud credentials:

```bash
TEMPORAL_ADDRESS=testnamespace.sdvdw.tmprl.cloud:7233
TEMPORAL_NAMESPACE=testnamespace.sdvdw
TEMPORAL_CERT_PATH="/path/to/file.pem"
TEMPORAL_KEY_PATH="/path/to/file.key"
````

## Configure Search Attributes
temporal operator search-attribute create --name "TRANSFER_EVENT_TYPE" --type keyword
temporal operator search-attribute create --name "TRANSFER_EVENT_STATUS" --type keyword
temporal operator search-attribute create --name "CORRELATION_ID" --type keyword

## Workflows
1.TransferReceipt
Temporal Workflow that orchestrates the underlying APIs, ultimately flushing to storage.

# Demo Script

## Run worker
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.TransferReceiptsWorker --console=plain

# Run starter
./gradlew -q execute -PmainClass=temporal.inventory.receiptsusecase.Starter --console=plain


