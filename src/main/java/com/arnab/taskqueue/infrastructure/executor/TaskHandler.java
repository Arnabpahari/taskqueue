package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;

public interface TaskHandler {

    TaskType getTaskType();

    String handle(Task task) throws Exception;
}