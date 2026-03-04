package com.arnab.taskqueue.application;

import com.arnab.taskqueue.api.dto.TaskStatusResponse;
import com.arnab.taskqueue.api.dto.TaskSubmissionRequest;
import com.arnab.taskqueue.api.dto.TaskSubmissionResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskSubmissionResponse submitTask(TaskSubmissionRequest request);

    TaskStatusResponse getTaskStatus(UUID taskId);

    List<TaskStatusResponse> listTasks(String status);
}