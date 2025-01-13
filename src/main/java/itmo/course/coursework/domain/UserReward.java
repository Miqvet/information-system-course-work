package itmo.course.coursework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class UserReward {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(optional = false)
    private Reward reward;

    @ManyToOne(optional = false)
    @JsonIgnore
    private User user;

    @Column
    @CreationTimestamp
    private LocalDateTime awardedDate;
} 