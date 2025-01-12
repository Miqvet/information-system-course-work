package itmo.course.coursework.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDTO {
    private Long taskId;
    private UserDTO user;
    private String comment;
}
