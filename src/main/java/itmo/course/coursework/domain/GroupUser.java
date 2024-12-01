package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class GroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long groupUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupUserRole role;

    @ManyToOne(optional = false)
    private Group group;

    @ManyToOne(optional = false)
    private User user;
} 