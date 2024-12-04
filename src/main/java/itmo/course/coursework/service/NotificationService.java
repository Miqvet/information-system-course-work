package itmo.course.coursework.service;

import itmo.course.coursework.domain.Notification;
import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.repository.NotificationRepository;
import itmo.course.coursework.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 300000) // Выполнять раз в 5 минут
    public void testik() {
        emailService.sendEmail("your_mom@mail.com", "dauzoc@tempmailto.org", "hello", "this is test");
    }

    @Scheduled(fixedRate = 300000) // Выполнять раз в 5 минут
    public void sendDeadlineNotifications() {
        List<Task> tasks = taskRepository.findByDeadlineBefore(LocalDateTime.now().plusMinutes(5));
        for (Task task : tasks) {
            for (UserTask userTask : task.getUserTasks())
                sendDeadlineNotification(userTask);
        }
    }

    private void sendDeadlineNotification(UserTask userTask) {
        User user = userTask.getUser();
        Task task = userTask.getTask();

        String subject = "Приближается дедлайн задачи!";
        String message = String.format(
                "Уважаемый %s,\n\nЗадача \"%s\" (ID: %d) должна быть выполнена к %s.\nПожалуйста, обратите внимание!\n\nС уважением,\nКоманда проекта",
                user.getFirstName(), task.getTitle(), task.getTaskId(), task.getDeadline().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        );

        Notification notification = new Notification();
        notification.setTitle(subject);
        notification.setDescription(message);
        notification.setDate(LocalDateTime.now());
        notification.setUserTask(userTask);
        notificationRepository.save(notification);

        emailService.sendEmail("your_mom@mail.com", user.getEmail(), subject, message);
    }
}
