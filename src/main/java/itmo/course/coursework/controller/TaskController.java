package itmo.course.coursework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import itmo.course.coursework.service.TaskService;
import itmo.course.coursework.service.UserTaskService;
import itmo.course.coursework.service.GroupService;
import itmo.course.coursework.service.UserService;
import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.domain.Group;
import itmo.course.coursework.dto.request.TaskCreateRequest;
import itmo.course.coursework.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final UserService userService;
    private final TaskService taskService;
    private final UserTaskService userTaskService;
    private final GroupService groupService;

    @GetMapping("/calendar")
    public ResponseEntity<List<Task>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        
        return ResponseEntity.ok(taskService.getTasksByDateRange(currentUser.getId(), start, end));
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<UserTask> completeTask(@PathVariable Long taskId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        
        return ResponseEntity.ok(userTaskService.updateTaskStatus(taskId, currentUser.getId(), true));
    }

    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<UserTask> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        Task task = taskService.getTaskById(taskId);
        User currentUser = userService.findByEmail(
            SecurityContextHolder.getContext().getAuthentication().getName());
            
        if (!groupService.isUserInGroup(task.getGroup(), currentUser)) {
            throw new BadRequestException("У вас нет прав для назначения задач в этой группе");
        }
        
        return ResponseEntity.ok(userTaskService.assignTaskToUser(taskId, userId));
    }

    @PostMapping("/group/{groupId}")
    public ResponseEntity<Task> createTask(
            @PathVariable Long groupId,
            @RequestBody TaskCreateRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);
        
        if (!groupService.isUserInGroup(group, currentUser)) {
            throw new BadRequestException("Вы не являетесь членом этой группы");
        }
        
        request.setGroupId(groupId);
        return ResponseEntity.ok(taskService.createTask(request));
    }
} 