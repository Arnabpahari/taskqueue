package com.arnab.taskqueue.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TaskSubmissionResponse {

    private UUID taskId;
    private String status;
}