package itmo.course.coursework.repository;

import itmo.course.coursework.domain.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
}