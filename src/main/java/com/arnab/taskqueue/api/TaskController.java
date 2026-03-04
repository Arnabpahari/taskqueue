package com.arnab.taskqueue.api;

import com.arnab.taskqueue.api.dto.TaskStatusResponse;
import com.arnab.taskqueue.api.dto.TaskSubmissionRequest;
import com.arnab.taskqueue.api.dto.TaskSubmissionResponse;
import com.arnab.taskqueue.application.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskSubmissionResponse submitTask(
            @Valid @RequestBody TaskSubmissionRequest request
    ) {
        return taskService.submitTask(request);
    }

    @GetMapping("/{id}")
    public TaskStatusResponse getTaskStatus(@PathVariable UUID id) {
        return taskService.getTaskStatus(id);
    }
    @GetMapping
    public List<TaskStatusResponse> listTasks(
            @RequestParam(required = false) String status
    ) {
        return taskService.listTasks(status);
    }
}
