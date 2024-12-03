package itmo.course.coursework.dto.request;

import itmo.course.coursework.domain.Category;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private Integer currentPriority;
    private LocalDateTime deadline;
    private Boolean isCompleted;
    private Boolean isRepeated;
    private Integer repeatedPeriod;
    private Category category;
    private Long assignedUserId;
    private Long groupId;
} 