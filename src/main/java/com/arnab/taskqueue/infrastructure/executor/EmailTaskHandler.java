package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailTaskHandler implements TaskHandler {

    private static final Logger log = LoggerFactory.getLogger(EmailTaskHandler.class);

    @Override
    public TaskType getTaskType() {
        return TaskType.EMAIL_SEND;
    }

    @Override
    public String handle(Task task) throws Exception {

        log.info("Executing EMAIL_SEND task {}", task.getId());
        log.debug("Payload: {}", task.getPayload());

        Thread.sleep(3000);

        return "Email sent successfully";
    }
}