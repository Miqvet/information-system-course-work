package itmo.course.coursework.dto.request;

import itmo.course.coursework.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class GroupCreateRequest {
    private String name;
    private String description;
    private List<User> members;
}
