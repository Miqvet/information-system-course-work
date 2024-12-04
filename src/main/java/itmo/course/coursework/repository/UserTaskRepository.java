package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.dto.response.TaskDTO;
import itmo.course.coursework.dto.response.TaskStatisticsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Query(value = "SELECT t.task_id, t.title, t.description, t.current_priority currentPriority, t.deadline, t.is_completed isCompleted FROM get_user_tasks(:userId, :completed, :priority) t", nativeQuery = true)
    List<Map<String, Object>> getUserTasksByFunction(@Param("userId") Long userId,
                                                     @Param("completed") Boolean completed,
                                                     @Param("priority") Integer priority);

    @Query(value = "SELECT s.total_tasks totalTasks, s.completed_tasks completedStatus, s.completion_rate completionRate, s.high_priority_tasks highPriorityTasks FROM get_user_task_statistics(:userId, :startDate, :endDate) s", nativeQuery = true)
    List<Map<String, Object>> getUserTaskStatistics(@Param("userId") Long userId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
} 