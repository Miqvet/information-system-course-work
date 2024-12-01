package itmo.course.coursework.dto.request;

import lombok.Data;

@Data
public class UserSignInRequest {
    private String email;
    private String password;
} 