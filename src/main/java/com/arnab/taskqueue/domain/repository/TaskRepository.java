package com.arnab.taskqueue.domain.repository;

import com.arnab.taskqueue.domain.model.Task;
import com.arnab.taskqueue.domain.model.TaskStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("""
        SELECT t
        FROM Task t
        WHERE t.status = :status
        ORDER BY t.priority DESC, t.createdAt ASC
    """)
    List<Task> findNextTask(TaskStatus status, PageRequest pageable);

    Optional<Task> findFirstByStatusOrderByPriorityDescCreatedAtAsc(TaskStatus status);

    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);
}