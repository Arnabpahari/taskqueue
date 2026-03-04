package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class CsvTaskHandler implements TaskHandler {

    @Override
    public TaskType getTaskType() {
        return TaskType.CSV_PROCESS;
    }

    @Override
    public String handle(Task task) throws Exception {

        System.out.println("Processing CSV with payload: " + task.getPayload());

        Thread.sleep(4000);

        return "CSV processed successfully";
    }
}