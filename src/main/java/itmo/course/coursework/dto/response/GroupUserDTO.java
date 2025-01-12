package itmo.course.coursework.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupUserDTO {
    private Long id;
    private String name;
    private String role;
} 