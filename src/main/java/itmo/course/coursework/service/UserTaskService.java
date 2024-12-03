package itmo.course.coursework.service;

import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserTaskService {
    private final UserTaskRepository userTaskRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final GroupService groupService;

    @Transactional
    public UserTask assignTaskToUser(Long taskId, Long userId) {
        Task task = taskService.getTaskById(taskId);
        User user = userService.getUserById(userId);
        
        if (!groupService.existsGroupUserByGroupAndUser(task.getGroup(), user)) {
            throw new BadRequestException("Пользователь не является членом группы задачи");
        }
        
        Optional<UserTask> existingAssignment = userTaskRepository
            .findUserTaskByUserAndTask(user, task);
            
        if (existingAssignment.isPresent()) {
            throw new BadRequestException("Задача уже назначена этому пользователю");
        }

        UserTask userTask = new UserTask();
        userTask.setTask(task);
        userTask.setUser(user);
        userTask.setAssignedDate(LocalDateTime.now());
        userTask.setCompletionStatus(false);

        return userTaskRepository.save(userTask);
    }

    public UserTask getUserTask(Long taskId, Long userId) {
        Task task = taskService.getTaskById(taskId);
        User user = userService.getUserById(userId);
        
        return userTaskRepository.findUserTaskByUserAndTask(user, task)
            .orElseThrow(() -> new BadRequestException("Назначение задачи не найдено"));
    }

    @Transactional
    public UserTask updateTaskStatus(Long taskId, Long userId, boolean completionStatus) {
        UserTask userTask = getUserTask(taskId, userId);
        
        if (!userTask.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Только назначенный пользователь может изменить статус задачи");
        }
        
        userTask.setCompletionStatus(completionStatus);
        return userTaskRepository.save(userTask);
    }

    @Transactional
    public void removeTaskAssignment(Long taskId, Long userId) {
        UserTask userTask = getUserTask(taskId, userId);
        userTaskRepository.delete(userTask);
    }

    public List<UserTask> getUserTasks(Long userId) {
        User user = userService.getUserById(userId);
        return userTaskRepository.findAllByUser(user);
    }

    public List<UserTask> getTaskAssignments(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return userTaskRepository.findAllByTask(task);
    }
} 