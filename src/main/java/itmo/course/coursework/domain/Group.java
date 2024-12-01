package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity @Table(name = "Group_")
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long groupId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(optional = false)
    private User createdBy;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupUser> groupUsers;
} 