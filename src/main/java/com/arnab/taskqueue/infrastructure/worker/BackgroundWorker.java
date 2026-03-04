package com.arnab.taskqueue.infrastructure.worker;

import com.arnab.taskqueue.application.TaskDispatcher;
import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskStatus;
import com.arnab.taskqueue.domain.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BackgroundWorker {

    private final TaskRepository taskRepository;
    private final TaskDispatcher taskDispatcher;

    @Scheduled(fixedDelay = 10000)
    //@Transactional
    public void processNextTask() {

        System.out.println("Worker triggered");

        Optional<Task> optionalTask =
                taskRepository.findFirstByStatusOrderByCreatedAtAsc(TaskStatus.PENDING);

        if (optionalTask.isEmpty()) {
            System.out.println("No pending task");
            return;
        }

        Task task = optionalTask.get();
        System.out.println("Processing task: " + task.getId());

        try {
            task.setStatus(TaskStatus.RUNNING);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);

            System.out.println("Task set to RUNNING");

            String result = taskDispatcher.dispatch(task);

            System.out.println("Handler finished");

            task.setResult(result);
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());

            taskRepository.save(task);

            System.out.println("Task set to COMPLETED");

        } catch (Exception e) {

            System.out.println("Exception occurred: " + e.getMessage());

            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());

            taskRepository.save(task);
        }
    }
}