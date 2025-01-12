package itmo.course.coursework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(optional = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column
    private LocalDateTime deadline;

    @Column(nullable = false)
    private Integer currentPriority;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isRepeated = false;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column
    private Integer repeatedPeriod;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Group group;
} 