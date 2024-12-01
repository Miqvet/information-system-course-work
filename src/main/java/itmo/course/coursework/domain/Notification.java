package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id_serial")
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(name = "user_task_id", nullable = false)
    private Integer userTaskId;
    
    private LocalDateTime date;
} 