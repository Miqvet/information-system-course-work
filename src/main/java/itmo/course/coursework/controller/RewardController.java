package itmo.course.coursework.controller;

import itmo.course.coursework.domain.User;
import itmo.course.coursework.domain.UserReward;
import itmo.course.coursework.service.RewardService;
import itmo.course.coursework.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserReward>> getUserRewards() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        return ResponseEntity.ok(rewardService.findAllUserRewards(currentUser));
    }
} 