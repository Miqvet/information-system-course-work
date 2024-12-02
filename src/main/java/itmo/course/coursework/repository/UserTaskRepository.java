package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    Optional<UserTask> findUserTaskByUserAndTask(User user, Task task);
} 