package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id_serial")
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "created_by")
    private Integer createdBy;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToMany(mappedBy = "groups")
    private Set<User> users;
} 