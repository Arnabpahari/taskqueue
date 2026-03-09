package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BulkDataImportHandler implements TaskHandler {

    private static final Logger log = LoggerFactory.getLogger(BulkDataImportHandler.class);

    @Override
    public TaskType getTaskType() {
        return TaskType.BULK_DATA_IMPORT;
    }

    @Override
    public String handle(Task task) throws Exception {

        log.info("Executing BULK_DATA_IMPORT task {}", task.getId());

        Thread.sleep(5000);

        return "Bulk data imported successfully";
    }
}
