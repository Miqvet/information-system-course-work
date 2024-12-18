package itmo.course.coursework.controller;

import itmo.course.coursework.dto.request.CommentCreateRequest;
import itmo.course.coursework.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import itmo.course.coursework.domain.Comment;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentCreateRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(commentService.createComment(request, userEmail));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Comment>> getTaskComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getTaskComments(taskId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody String newComment) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(commentService.updateComment(commentId, newComment, userEmail));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.deleteComment(commentId, userEmail);
        return ResponseEntity.noContent().build();
    }
} 