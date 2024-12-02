package itmo.course.coursework.dto.request;

import lombok.Data;

@Data
public class AddGroupMemberRequest {
    private Long groupId;
    private Long userId;
    private String userRole;
}
