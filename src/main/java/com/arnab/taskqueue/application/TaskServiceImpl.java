package com.arnab.taskqueue.application;

import com.arnab.taskqueue.api.dto.TaskStatusResponse;
import com.arnab.taskqueue.api.dto.TaskSubmissionRequest;
import com.arnab.taskqueue.api.dto.TaskSubmissionResponse;
import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.arnab.taskqueue.domain.model.TaskStatus;
import com.arnab.taskqueue.domain.model.Task;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskSubmissionResponse submitTask(TaskSubmissionRequest request) {

        Task task = Task.builder()
                .type(request.getType())
                .payload(request.getPayload())
                .priority(request.getPriority())
                .parentTaskId(request.getParentTaskId())
                .build();

        Task savedTask = taskRepository.save(task);

        return new TaskSubmissionResponse(
                savedTask.getId(),
                savedTask.getStatus().name()
        );
    }

    @Override
    public TaskStatusResponse getTaskStatus(UUID taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return TaskStatusResponse.builder()
                .id(task.getId())
                .type(task.getType())
                .status(task.getStatus())
                .result(task.getResult())
                .errorMessage(task.getErrorMessage())
                .createdAt(task.getCreatedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }

    @Override
    public List<TaskStatusResponse> listTasks(String status) {

        List<Task> tasks;

        if (status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskRepository.findByStatusOrderByCreatedAtDesc(taskStatus);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(task -> TaskStatusResponse.builder()
                        .id(task.getId())
                        .type(task.getType())
                        .status(task.getStatus())
                        .result(task.getResult())
                        .errorMessage(task.getErrorMessage())
                        .createdAt(task.getCreatedAt())
                        .startedAt(task.getStartedAt())
                        .completedAt(task.getCompletedAt())
                        .build())
                .toList();
    }
}