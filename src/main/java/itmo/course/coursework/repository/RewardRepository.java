package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Reward;
import itmo.course.coursework.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findAllByUser(User user);
}