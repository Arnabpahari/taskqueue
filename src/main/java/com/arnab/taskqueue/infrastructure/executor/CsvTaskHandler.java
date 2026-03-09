package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CsvTaskHandler implements TaskHandler {

    private static final Logger log = LoggerFactory.getLogger(CsvTaskHandler.class);

    @Override
    public TaskType getTaskType() {
        return TaskType.CSV_PROCESS;
    }

    @Override
    public String handle(Task task) throws Exception {

        log.info("Executing CSV_PROCESS task {}", task.getId());

        Thread.sleep(4000);

        return "CSV processed successfully";
    }
}