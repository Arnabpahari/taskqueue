package com.arnab.taskqueue.api.dto;

import com.arnab.taskqueue.domain.model.TaskType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TaskSubmissionRequest {

    @NotNull
    private TaskType type;

    @NotNull
    private String payload;

    private Integer priority;

    private UUID parentTaskId;
}