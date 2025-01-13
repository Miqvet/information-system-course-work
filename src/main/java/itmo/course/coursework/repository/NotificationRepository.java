package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Notification;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserTaskUser(User user);

    void deleteAllByUserTaskTaskId(Long taskId);

    void deleteAllByUserTask(UserTask userTask);
}