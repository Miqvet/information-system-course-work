package itmo.course.coursework.controller;

import itmo.course.coursework.domain.User;
import itmo.course.coursework.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.findByEmail(userEmail));
    }

    @PutMapping("/current")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User updatedUser) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        
        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        
        return ResponseEntity.ok(userService.saveUser(currentUser));
    }
} 