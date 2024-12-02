package itmo.course.coursework.dto.response;

import itmo.course.coursework.domain.GroupUserRole;
import lombok.Data;

@Data
public class GroupUserResponse {
    private Long groupUserId;
    private Long groupId;
    private Long userId;
    private GroupUserRole userRole;
}
