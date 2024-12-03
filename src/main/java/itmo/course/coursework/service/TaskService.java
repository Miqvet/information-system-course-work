package itmo.course.coursework.service;

import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.dto.request.TaskCreateRequest;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.TaskRepository;
import itmo.course.coursework.repository.UserRepository;
import itmo.course.coursework.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    private final UserTaskRepository userTaskRepository;

    private final GroupService groupService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public Task createTask(TaskCreateRequest request, Long groupId) {
        validateTaskRequest(request);
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCurrentPriority(request.getCurrentPriority());
        task.setDeadline(request.getDeadline());
        task.setCategory(request.getCategory());
        task.setIsCompleted(false);
        task.setIsRepeated(request.getIsRepeated());
        task.setRepeatedPeriod(request.getRepeatedPeriod());
        
        Task savedTask = taskRepository.save(task);

        // Если указан пользователь для назначения
        if (request.getAssignedUserId() != null) {
            assignTaskToUser(savedTask, request.getAssignedUserId(), groupId);
        }

        return savedTask;
    }

    @Transactional
    public UserTask assignTaskToUser(Task task, Long userId, Long groupId) {
        // Проверяем, что пользователь существует и находится в группе
        User user = userService.getUserById(userId);
//        if (!groupService.isUserInGroup(userId, groupId)) {
//            throw new BadRequestException("Пользователь не является членом группы");
//        }

        UserTask userTask = new UserTask();
        userTask.setTask(task);
        userTask.setUser(user);
        userTask.setAssignedDate(LocalDateTime.now());
        userTask.setCompletionStatus(false);

        return userTaskRepository.save(userTask);
    }

    @Transactional
    public Task completeTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BadRequestException("Задача не найдена"));

        UserTask userTask = userTaskRepository.findUserTaskByUserAndTask(userRepository.findById(userId).get(),task)
            .orElseThrow(() -> new BadRequestException("Задача не назначена данному пользователю"));

        userTask.setCompletionStatus(true);
        task.setIsCompleted(true);

        userTaskRepository.save(userTask);
        return taskRepository.save(task);
    }

    public Page<Task> getTasksByDeadlinePeriod(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return taskRepository.findByDeadlineBetweenOrderByDeadlineAsc(start, end, pageable);
    }

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
} 