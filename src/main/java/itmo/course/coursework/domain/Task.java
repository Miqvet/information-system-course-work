package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id_serial")
    private Long id;
    
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "text")
    private String description;
    
    private LocalDateTime deadline;
    
    @Column(name = "current_priority")
    private Integer currentPriority;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "is_repeated")
    private Boolean isRepeated;
    
    @Column(name = "repeated_period")
    private Integer repeatedPeriod;
    
    @OneToMany(mappedBy = "task")
    private Set<UserTask> userTasks;
    
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
} 