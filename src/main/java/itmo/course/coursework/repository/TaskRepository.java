package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Category;
import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCategory(Category category);

    List<Task> findByUserTasksUserUserIdAndDeadlineBetweenOrderByDeadlineAsc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Task> findByGroupAndDeadlineBetweenOrderByDeadlineAsc(Group group, LocalDateTime start, LocalDateTime end);

    List<Task> findByDeadlineBefore(LocalDateTime dateTime);
} 