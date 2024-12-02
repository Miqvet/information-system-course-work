package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Category;
import itmo.course.coursework.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCategory(Category category);
    List<Task> findByIsCompletedAndIsRepeatedTrue(Boolean isCompleted);
    Page<Task> findByDeadlineBetweenOrderByDeadlineAsc(LocalDateTime start, LocalDateTime end, Pageable pageable);
} 