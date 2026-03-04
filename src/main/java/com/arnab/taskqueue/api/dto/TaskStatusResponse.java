package com.arnab.taskqueue.api.dto;

import com.arnab.taskqueue.domain.model.TaskStatus;
import com.arnab.taskqueue.domain.model.TaskType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TaskStatusResponse {

    private UUID id;
    private TaskType type;
    private TaskStatus status;
    private String result;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
