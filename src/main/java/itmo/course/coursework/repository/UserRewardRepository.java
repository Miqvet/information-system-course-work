package itmo.course.coursework.repository;

import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findAllByUser(User user);
}