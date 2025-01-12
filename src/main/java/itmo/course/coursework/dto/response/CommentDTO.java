package itmo.course.coursework.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {
    private Long taskId;
    private UserDTO user;
    private String comment;
    private LocalDateTime createdAt;
}
