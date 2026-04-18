package com.arnab.taskqueue.infrastructure.worker;

import com.arnab.taskqueue.application.TaskDispatcher;
import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskStatus;
import com.arnab.taskqueue.domain.repository.TaskRepository;
import com.arnab.taskqueue.infrastructure.webhook.WebhookService;
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
    private final WebhookService webhookService;

    @Scheduled(fixedDelay = 15000)
    public void processNextTask() {

        // STEP 0 — RETRY WEBHOOKS FIRST
        List<Task> retryTasks = taskRepository.findAll().stream()
                .filter(t ->
                        t.getWebhookUrl() != null &&
                                !Boolean.TRUE.equals(t.getWebhookDelivered()) &&
                                t.getWebhookAttempts() < 3 &&
                                (t.getStatus() == TaskStatus.COMPLETED || t.getStatus() == TaskStatus.FAILED)
                )
                .toList();

        if (!retryTasks.isEmpty()) {
            Task retryTask = retryTasks.get(0);

            log.info("Retrying webhook for task {}", retryTask.getId());

            boolean delivered = webhookService.sendWebhook(retryTask);

            retryTask.setWebhookAttempts(retryTask.getWebhookAttempts() + 1);
            retryTask.setWebhookDelivered(delivered);

            taskRepository.save(retryTask);
            return; // Retry happens instead of picking new task
        }

        // STEP 1 — NORMAL TASK PICKING
        List<Task> tasks = taskRepository.findNextTask(
                TaskStatus.PENDING,
                PageRequest.of(0, 1)
        );

        if (tasks.isEmpty()) {
            log.debug("No pending tasks found");
            return;
        }

        Task task = tasks.get(0);

        log.info("Picked task {} priority {}", task.getId(), task.getPriority());

        try {

            // STEP 2 — DEPENDENCY CHECK
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

            // STEP 3 — RUN TASK
            task.setStatus(TaskStatus.RUNNING);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);

            String result = taskDispatcher.dispatch(task);

            // SUCCESS BLOCK (COMPLETED)
            task.setResult(result);
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());

            taskRepository.save(task);

            log.info("Task {} completed", task.getId());

            // WEBHOOK AFTER SUCCESS
            boolean delivered = webhookService.sendWebhook(task);

            task.setWebhookAttempts(task.getWebhookAttempts() + 1);
            task.setWebhookDelivered(delivered);

            taskRepository.save(task);

        } catch (Exception e) {

            // FAILURE BLOCK (FAILED)
            log.error("Task {} failed", task.getId(), e);

            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setCompletedAt(LocalDateTime.now());

            taskRepository.save(task);

            // WEBHOOK AFTER FAILURE
            boolean delivered = webhookService.sendWebhook(task);

            task.setWebhookAttempts(task.getWebhookAttempts() + 1);
            task.setWebhookDelivered(delivered);

            taskRepository.save(task);
        }
    }
}