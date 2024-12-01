package itmo.course.coursework.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user_reward")
public class UserReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_reward_id_serial")
    private Long id;
    
    @Column(nullable = false)
    private Integer userId;
    
    @Column(nullable = false)
    private Integer rewardId;
    
    @Column(name = "awarded_date")
    private LocalDateTime awardedDate = LocalDateTime.now();
} 