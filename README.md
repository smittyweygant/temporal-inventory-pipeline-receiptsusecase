## Mock Transfer Reciept Pipeline - Nordstrom

This repo is to develop workflows for Nordstrom's usecases described by EPAM here: [a link] (https://docs.google.com/document/d/1Nq_ZPsC2p7fxeQcueEgFCz3YIGVwRlxnizQOV37Sg6o/edit?usp=sharing) transfer_receipt flow description and challenges

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

## Workflows
1.TransferReceipt
Temporal Workflow that orchestrates the underlying APIs, ultimately flushing to storage.
2.TransferReceiptSupervisor
A Temporal Workflow with one activity...a consumer of the Kafka topic that routes/starts TransferReceipt Workflows.

