package itmo.course.coursework.service;

import itmo.course.coursework.domain.Reward;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserReward;
import itmo.course.coursework.repository.RewardRepository;
import itmo.course.coursework.repository.UserRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {
    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;

    public List<Reward> findAllUserRewards(User user) {
        return rewardRepository.findAllByUser(user);
    }

    public void assignRewardToUser(Reward reward, User user) {
        UserReward userReward = new UserReward();
        userReward.setUser(user);
        userReward.setReward(reward);
        userReward.setAwardedDate(LocalDateTime.now());
        userRewardRepository.save(userReward);
    }
}
