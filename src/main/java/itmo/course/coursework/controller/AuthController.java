package itmo.course.coursework.controller;

import itmo.course.coursework.domain.User;
import itmo.course.coursework.dto.request.UserRegistrationRequest;
import itmo.course.coursework.dto.request.UserSignInRequest;
import itmo.course.coursework.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserSignInRequest request) {
        authService.authenticate(request);
        return ResponseEntity.ok("Successfully authenticated");
    }
} 