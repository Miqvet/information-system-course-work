package itmo.course.coursework.service;

import itmo.course.coursework.domain.Category;
import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.Task;
import itmo.course.coursework.dto.request.TaskCreateRequest;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
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
        if (!groupService.isUserInGroup(group, userService.getUserById(request.getAssignedUserId()))) {
            throw new BadRequestException("Вы не являетесь членом этой группы");
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

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return taskRepository.findByUserTasksUserUserIdAndDeadlineBetweenOrderByDeadlineAsc(userId, start, end);
    }
} 