package itmo.course.coursework.dto.request;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private Long taskId;
    private String comment;
} 