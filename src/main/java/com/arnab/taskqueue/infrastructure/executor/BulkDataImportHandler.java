package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class BulkDataImportHandler implements TaskHandler {

    @Override
    public TaskType getTaskType() {
        return TaskType.BULK_DATA_IMPORT;
    }

    @Override
    public String handle(Task task) throws Exception {

        System.out.println("Running bulk import with payload: " + task.getPayload());

        Thread.sleep(5000);

        return "Bulk data imported successfully";
    }
}
