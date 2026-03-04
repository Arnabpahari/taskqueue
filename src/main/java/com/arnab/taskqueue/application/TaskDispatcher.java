package com.arnab.taskqueue.application;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import com.arnab.taskqueue.infrastructure.executor.TaskHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TaskDispatcher {

    private final Map<TaskType, TaskHandler> handlerMap;

    public TaskDispatcher(List<TaskHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(TaskHandler::getTaskType, h -> h));
    }

    public String dispatch(Task task) throws Exception {

        TaskHandler handler = handlerMap.get(task.getType());

        if (handler == null) {
            throw new RuntimeException("No handler found for task type: " + task.getType());
        }

        return handler.handle(task);
    }
}
