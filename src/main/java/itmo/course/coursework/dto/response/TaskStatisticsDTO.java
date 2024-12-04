package itmo.course.coursework.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TaskStatisticsDTO {
    private Long totalTasks;
    private Long completedTasks;
    private Double completionRate;
    private Long highPriorityTasks;
}