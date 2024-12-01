package itmo.course.coursework.service;

import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.dto.request.TaskCreateRequest;
import itmo.course.coursework.repository.TaskRepository;
import itmo.course.coursework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Task createTask(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCurrentPriority(request.getCurrentPriority());
        task.setDeadline(request.getDeadline());
        task.setCategory(request.getCategory());
        task.setCreatedAt(LocalDateTime.now());
        task.setIsCompleted(false);

        Task savedTask = taskRepository.save(task);

        if (request.getAssignedUserId() != null) {
            assignTaskToUser(savedTask, request.getAssignedUserId());
        }

        return savedTask;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void assignTaskToUser(Task task, Long userId) {
        UserTask userTask = new UserTask();
        userTask.setTask(task);
        userTask.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        userTask.setAssignedDate(LocalDateTime.now());
        userTask.setCompletionStatus(false);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setIsCompleted(true);
        taskRepository.save(task);
    }
} 