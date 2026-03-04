package com.arnab.taskqueue.domain.repository;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Optional<Task> findFirstByStatusOrderByCreatedAtAsc(TaskStatus status);

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);
}