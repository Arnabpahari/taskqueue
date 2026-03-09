package com.arnab.taskqueue.infrastructure.worker;

import com.arnab.taskqueue.application.TaskDispatcher;
import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskStatus;
import com.arnab.taskqueue.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BackgroundWorker {

    private static final Logger log = LoggerFactory.getLogger(BackgroundWorker.class);

    private final TaskRepository taskRepository;
    private final TaskDispatcher taskDispatcher;

    @Scheduled(fixedDelay = 15000)
    public void processNextTask() {

        List<Task> tasks = taskRepository.findNextTask(
                TaskStatus.PENDING,
                PageRequest.of(0,1)
        );

        if (tasks.isEmpty()) {
            log.debug("No pending tasks found");
            return;
        }

        Task task = tasks.get(0);

        log.info("Picked task {} priority {}", task.getId(), task.getPriority());

        try {

            UUID parentId = task.getParentTaskId();

            if (parentId != null) {

                Optional<Task> parentTask = taskRepository.findById(parentId);

                if (parentTask.isEmpty()) {
                    log.warn("Parent {} not found for child {}", parentId, task.getId());
                    return;
                }

                Task parent = parentTask.get();

                if (parent.getStatus() == TaskStatus.FAILED ||
                        parent.getStatus() == TaskStatus.CANCELLED) {

                    log.warn("Parent {} failed. Cancelling child {}", parentId, task.getId());

                    task.setStatus(TaskStatus.CANCELLED);
                    task.setCompletedAt(LocalDateTime.now());
                    taskRepository.save(task);
                    return;
                }

                if (parent.getStatus() != TaskStatus.COMPLETED) {
                    log.info("Task {} waiting for parent {}", task.getId(), parentId);
                    return;
                }
            }

            task.setStatus(TaskStatus.RUNNING);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);

            String result = taskDispatcher.dispatch(task);

            task.setResult(result);
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);

            log.info("Task {} completed", task.getId());

        } catch (Exception e) {

            log.error("Task {} failed", task.getId(), e);

            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }
}