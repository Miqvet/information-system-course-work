package itmo.course.coursework.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserTaskDTO {
    private Long id;
    private Long taskId;
    private Long group;
    private String title;
    private String description;
    private Integer priority;
    private LocalDateTime deadline;
    private Boolean completed;

    public UserTaskDTO(Long id, String title, String description, Integer priority, LocalDateTime deadline, Boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = completed;
    }

}