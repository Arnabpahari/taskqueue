package com.arnab.taskqueue.infrastructure.executor;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class EmailTaskHandler implements TaskHandler {

    @Override
    public TaskType getTaskType() {
        return TaskType.EMAIL_SEND;
    }

    @Override
    public String handle(Task task) throws Exception {

        System.out.println("Sending email with payload: " + task.getPayload());

        Thread.sleep(3000);

        return "Email sent successfully";
    }
}