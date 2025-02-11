package itmo.course.coursework.controller;

import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.GroupUser;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.dto.request.AddGroupMemberRequest;
import itmo.course.coursework.dto.request.FindAllGroupMembersRequest;
import itmo.course.coursework.dto.request.FindAllUserGroupsRequest;
import itmo.course.coursework.dto.request.GroupCreateRequest;
import itmo.course.coursework.dto.request.GroupUpdateRequest;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.service.GroupService;
import itmo.course.coursework.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import itmo.course.coursework.dto.response.GroupUserDTO;
import itmo.course.coursework.dto.response.GroupDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody GroupCreateRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }
    @GetMapping
    public ResponseEntity<List<Group>> getUserGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userService.findByEmail(userEmail);
        FindAllUserGroupsRequest request = new FindAllUserGroupsRequest();
        request.setUserId(currentUser.getId());
        return ResponseEntity.ok(groupService.findAllUserGroups(request));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupUser> addMember(@PathVariable Long groupId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        
        AddGroupMemberRequest request = new AddGroupMemberRequest();
        request.setGroupId(groupId);
        request.setUserId(currentUser.getId());
        request.setUserRole("MEMBER");
        
        return ResponseEntity.ok(groupService.addGroupMember(request));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupUser>> getGroupMembers(@PathVariable Long groupId) {
        FindAllGroupMembersRequest request = new FindAllGroupMembersRequest();
        request.setGroupId(groupId);
        return ResponseEntity.ok(groupService.findAllGroupMembers(request));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable Long groupId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);
        
        if (!groupService.isUserInGroup(group, currentUser)) {
            throw new BadRequestException("Вы не являетесь членом этой группы");
        }
        
        return ResponseEntity.ok(new GroupDTO(
            group.getId(),
            group.getName(),
            group.getDescription(),
            group.getCreatedBy().getFirstName() + " " + group.getCreatedBy().getLastName()
        ));
    }

    @DeleteMapping("/{groupId}/delete")
    public ResponseEntity<Boolean> deleteGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.deleteGroupById(groupId));
    }

    @DeleteMapping("/{groupId}/delete_member/{userId}")
    public ResponseEntity<Boolean> deleteMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);
        
        if (!groupService.isUserAdmin(group, currentUser)) {
            throw new BadRequestException("Только администратор может удалять участников");
        }
        
        return ResponseEntity.ok(groupService.deleteMember(groupId, userId));
    }

    @GetMapping("/{groupId}/users")
    public ResponseEntity<List<GroupUserDTO>> getGroupUsers(@PathVariable Long groupId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);
        
        if (!groupService.isUserInGroup(group, currentUser)) {
            throw new BadRequestException("Вы не являетесь членом этой группы");
        }
        FindAllGroupMembersRequest request = new FindAllGroupMembersRequest();
        request.setGroupId(groupId);
        
        List<GroupUser> groupUsers = groupService.findAllGroupMembers(request);
        return ResponseEntity.ok(groupUsers.stream()
            .map(gu -> new GroupUserDTO(
                gu.getUser().getId(),
                gu.getUser().getFirstName(),
                gu.getUser().getLastName(),
                gu.getRole().toString()
            ))
            .collect(Collectors.toList()));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long groupId, @RequestBody GroupUpdateRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);
        
        if (!groupService.isUserAdmin(group, currentUser)) {
            throw new BadRequestException("Только администратор может обновлять информацию о группе");
        }
        
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        
        Group updatedGroup = groupService.updateGroup(group);
        return ResponseEntity.ok(new GroupDTO(
            updatedGroup.getId(),
            updatedGroup.getName(),
            updatedGroup.getDescription(),
            updatedGroup.getCreatedBy().getFirstName() + " " + updatedGroup.getCreatedBy().getLastName()
        ));
    }

    @PutMapping("/{groupId}/update_role/{userId}")
    public ResponseEntity<GroupUser> updateMemberRole(
        @PathVariable Long groupId,
        @PathVariable Long userId,
        @RequestBody Map<String, String> request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);
        
        if (!groupService.isUserAdmin(group, currentUser)) {
            throw new BadRequestException("Только администратор может изменять роли участников");
        }
        
        return ResponseEntity.ok(groupService.updateMemberRole(groupId, userId, request.get("role")));
    }
    
} 