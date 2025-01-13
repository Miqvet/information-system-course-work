package itmo.course.coursework.service;

import itmo.course.coursework.domain.*;
import itmo.course.coursework.dto.request.TaskCreateRequest;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.GroupUserRepository;
import itmo.course.coursework.repository.TaskRepository;
import itmo.course.coursework.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    private final GroupService groupService;
    private final UserService userService;
    private final UserTaskRepository userTaskRepository;
    private final GroupUserRepository groupUserRepository;

    private void validateTaskRequest(TaskCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Название задачи обязательно");
        }
        if (request.getCurrentPriority() == null) {
            throw new BadRequestException("Приоритет задачи обязателен");
        }
        if (request.getDeadline() != null && request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дедлайн не может быть в прошлом");
        }
    }

    @Transactional
    public Task createTask(TaskCreateRequest request) {
        validateTaskRequest(request);

        Group group = groupService.findGroupById(request.getGroupId());
        User user = userService.getUserById(request.getAssignedUserId());
        if (!groupService.isUserInGroup(group, user)) {
            throw new BadRequestException("Вы не являетесь членом этой группы");
        }

        if (!groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN, user, group)) {
            throw new BadRequestException("Создавать задачи может только админ группы");
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCurrentPriority(request.getCurrentPriority());
        task.setDeadline(request.getDeadline());
        task.setIsRepeated(request.getIsRepeated() != null ? request.getIsRepeated() : false);
        task.setRepeatedPeriod(request.getRepeatedPeriod());
        task.setCategory(request.getCategory());
        task.setGroup(group);

        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Задача не найдена"));
    }

    public List<Task> getTasksByCategory(Category category) {
        return taskRepository.findByCategory(category);
    }

    @Transactional
    public Task updateTask(Long id, TaskCreateRequest request) {
        Task task = getTaskById(id);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getCurrentPriority() != null) {
            task.setCurrentPriority(request.getCurrentPriority());
        }
        if (request.getDeadline() != null) {
            if (request.getDeadline().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Дедлайн не может быть в прошлом");
            }
            task.setDeadline(request.getDeadline());
        }
        if (request.getIsRepeated() != null) {
            task.setIsRepeated(request.getIsRepeated());
        }
        if (request.getRepeatedPeriod() != null) {
            task.setRepeatedPeriod(request.getRepeatedPeriod());
        }

        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);

        Task task = getTaskById(id);
        if (!groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN, currentUser, task.getGroup())) {
            throw new BadRequestException("Только администратор группы может удалять задачи");
        }

        userTaskRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);

        return true;
    }

    public List<UserTask> getTasksByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return userTaskRepository.findByUserIdAndTaskDeadlineBetweenOrderByTaskDeadlineAsc(userId, start, end);
    }

    //@Scheduled(fixedRate = 300000)
    public void updateRepeatableTasks() {
        List<Task> tasks = taskRepository.findByIsCompletedFalseAndIsRepeatedTrue();
        for (Task task : tasks) {
            List<UserTask> userTasks = userTaskRepository.findAllByTask(task);
            for (UserTask userTask : userTasks) {
                if (userTask.getAssignedDate().isBefore(LocalDateTime.now().minusDays(task.getRepeatedPeriod()))) {
                    UserTask newUserTask = new UserTask();
                    newUserTask.setUser(userTask.getUser());
                    newUserTask.setTask(userTask.getTask());
                    newUserTask.setCompletionStatus(false);
                    newUserTask.setAssignedDate(LocalDateTime.now());
                    userTaskRepository.save(newUserTask);
                }
            }
        }
    }
} 