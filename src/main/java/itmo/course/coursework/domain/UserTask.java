package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class UserTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(optional = false)
    private Task task;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime assignedDate;

    @Column(nullable = false)
    private Boolean completionStatus = false;
} 