package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    Optional<UserTask> findUserTaskByUserAndTask(User user, Task task);

    List<UserTask> findAllByUser(User user);

    List<UserTask> findAllByTask(Task task);

    List<UserTask> findByTaskDeadlineBetweenAndCompletionStatusFalse(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT assign_task_to_user(:taskId, :userId, :priority)", nativeQuery = true)
    boolean assignTaskToUser(@Param("taskId") Long taskId,
                             @Param("userId") Long userId,
                             @Param("priority") Integer priority);

    @Query(value = """
            SELECT t.id as taskId, 
                   t.title, 
                   t.description, 
                   t.current_priority as currentPriority, 
                   t.deadline, 
                   t.is_completed as isCompleted
            FROM get_user_tasks(:userId, :completed, :priority)
            """, nativeQuery = true)
    List<Object[]> getUserTasksByFunction(@Param("userId") Long userId,
                                          @Param("completed") Boolean completed,
                                          @Param("priority") Integer priority);

    @Query(value = """
            SELECT total_tasks as totalTasks,
                   completed_tasks as completedTasks,
                   completion_rate as completionRate,
                   high_priority_tasks as highPriorityTasks
            FROM get_user_task_statistics(:userId, :startDate, :endDate)
            """, nativeQuery = true)
    Object[] getUserTaskStatistics(@Param("userId") Long userId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
} 