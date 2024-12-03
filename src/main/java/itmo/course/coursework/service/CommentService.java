package itmo.course.coursework.service;

import itmo.course.coursework.dto.request.CommentCreateRequest;
import itmo.course.coursework.domain.Comment;
import itmo.course.coursework.domain.Task;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final GroupService groupService;

    @Transactional
    public Comment createComment(CommentCreateRequest request, String userEmail) {
        Task task = taskService.getTaskById(request.getTaskId());
        User user = userService.findByEmail(userEmail);

        // Проверяем, является ли пользователь членом группы
        if (!groupService.isUserInGroup(task.getGroup(), user)) {
            throw new BadRequestException("Вы не можете комментировать задачи в этой группе");
        }

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setComment(request.getComment());

        return commentRepository.save(comment);
    }

    public List<Comment> getTaskComments(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return commentRepository.findByTaskOrderByCreatedAtDesc(task);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new BadRequestException("Комментарий не найден"));
    }

    @Transactional
    public Comment updateComment(Long commentId, String newComment, String userEmail) {
        Comment comment = getCommentById(commentId);
        User user = userService.findByEmail(userEmail);

        if (!comment.getUser().equals(user)) {
            throw new BadRequestException("Вы можете редактировать только свои комментарии");
        }

        comment.setComment(newComment);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = getCommentById(commentId);
        User user = userService.findByEmail(userEmail);

        if (!comment.getUser().equals(user)) {
            throw new BadRequestException("Вы можете удалять только свои комментарии");
        }

        commentRepository.delete(comment);
    }
} 