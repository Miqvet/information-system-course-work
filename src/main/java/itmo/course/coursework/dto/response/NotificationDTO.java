package itmo.course.coursework.dto.response;

import itmo.course.coursework.domain.UserTask;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class NotificationDTO {
    private String title;
    private String description;
    private Long userTaskId;
    private Long groupId;
    private LocalDateTime date;
}
