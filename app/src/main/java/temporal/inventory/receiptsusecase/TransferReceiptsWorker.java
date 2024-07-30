package temporal.inventory.receiptsusecase;

/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class TransferReceiptsWorker {
    public static void main(String[] args) {
        // Create a gRPC stubs wrapper that talks to the local Docker instance of Temporal service.
        // WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Worker factory is used to create workers for specific task queues.
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Worker is created to poll the task queue.
        Worker worker = factory.newWorker("TRANSFER_RECEIPTS_TASK_QUEUE");

        // Register the workflow implementation with the worker.
        worker.registerWorkflowImplementationTypes(TransferMessageWorkflowImpl.class);
        worker.registerWorkflowImplementationTypes(TransferReceiptWorkflowImpl.class);

        // Register the activity implementation with the worker.
        worker.registerActivitiesImplementations(new ActivitiesImpl());

        // Start all the workers registered for a specific task queue.
        factory.start();
    }
}

