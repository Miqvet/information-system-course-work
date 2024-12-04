package itmo.course.coursework.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Integer currentPriority;
    private LocalDateTime deadline;
    private Boolean isCompleted;
} 