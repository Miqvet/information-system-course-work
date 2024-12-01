package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "group_user")
public class GroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_user_id_serial")
    private Long id;
    
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(length = 100)
    private String role;
    
    @ManyToOne
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Group group;
    
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
} 