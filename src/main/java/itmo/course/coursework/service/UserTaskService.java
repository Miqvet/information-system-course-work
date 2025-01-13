package itmo.course.coursework.service;

import itmo.course.coursework.domain.GroupUserRole;
import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserTask;
import itmo.course.coursework.dto.response.UserTaskDTO;
import itmo.course.coursework.dto.response.TaskStatisticsDTO;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.GroupUserRepository;
import itmo.course.coursework.repository.NotificationRepository;
import itmo.course.coursework.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserTaskService {
    private final UserTaskRepository userTaskRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final GroupService groupService;
    private final GroupUserRepository groupUserRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public UserTask assignTaskToUser(Long taskId, Long userId) {
        Task task = taskService.getTaskById(taskId);
        User user = userService.getUserById(userId);
        
        if (!groupService.isUserInGroup(task.getGroup(), user)) {
            throw new BadRequestException("Пользователь не является членом группы задачи");
        }
        if (groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN , user, task.getGroup())) {
            throw new BadRequestException("Назначать задачи может только админ группы");
        }


        Optional<UserTask> existingAssignment = userTaskRepository.findUserTaskByUserAndTask(user, task);
            
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
    public boolean updateTaskStatus(Long taskId, Long userId, boolean completionStatus) {
        UserTask userTask = getUserTask(taskId, userId);
        
        if (!userTask.getUser().getId().equals(userId)) {
            throw new BadRequestException("Только назначенный пользователь может изменить статус задачи");
        }
        
        userTask.setCompletionStatus(completionStatus);
        userTaskRepository.save(userTask);
        return true;
    }

    @Transactional
    public void removeTaskAssignment(Long taskId, Long userId) {
        UserTask userTask = getUserTask(taskId, userId);
        userTaskRepository.delete(userTask);
    }

    public List<UserTaskDTO> getUserTasks(Long userId) {
        User user = userService.getUserById(userId);
        List<UserTask> userTasks = userTaskRepository.findAllByUser(user);
        return userTasks.stream().map(userTask -> UserTaskDTO.builder()
                .id(userTask.getId())
                .taskId(userTask.getTask().getId())
                .group(userTask.getTask().getGroup())
                .title(userTask.getTask().getTitle())
                .description(userTask.getTask().getDescription())
                .deadline(userTask.getTask().getDeadline())
                .priority(userTask.getTask().getCurrentPriority())
                .completed(userTask.getCompletionStatus())
                .build()).toList();
    }

    public List<UserTask> getTaskAssignments(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return userTaskRepository.findAllByTask(task);
    }

    @Transactional
    public boolean assignTaskToUserByFunction(Long taskId, Long userId, Integer priority) {
        return userTaskRepository.assignTaskToUser(taskId, userId, priority);
    }

    @Transactional(readOnly = true)
    public List<UserTaskDTO> getUserTasksByFunction(Long userId, Boolean completed, Integer priority) {
        var tasks = new ArrayList<UserTaskDTO>();
        var results = userTaskRepository.getUserTasksByFunction(userId, completed, priority);
        for (var result : results)
            tasks.add(new UserTaskDTO((Long) result.get("task_id"), (String) result.get("title"), (String) result.get("description"), (Integer) result.get("priority"), ((Timestamp) result.get("deadline")).toLocalDateTime(), (Boolean) result.get("isCompleted")));
        return tasks;
    }

    @Transactional(readOnly = true)
    public TaskStatisticsDTO getUserTaskStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        var result = userTaskRepository.getUserTaskStatistics(userId, startDate, endDate).get(0);
        return new TaskStatisticsDTO((Long) result.get("totalTasks"), (Long) result.get("completedTasks"), ((BigDecimal) result.get("completionRate")).doubleValue(), (Long) result.get("highPriorityTasks"));
    }

    @Transactional
    public boolean deleteUserTask(Long taskId, Long userId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);

        UserTask userTask = getUserTask(taskId, userId);
        if (!groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN, currentUser, userTask.getTask().getGroup())) {
            throw new BadRequestException("Только администратор группы может удалять задачи");
        }

        userTaskRepository.delete(userTask);
        return true;
    }
}