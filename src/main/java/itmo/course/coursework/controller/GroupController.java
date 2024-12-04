package itmo.course.coursework.controller;

import itmo.course.coursework.domain.GroupUserRole;
import itmo.course.coursework.repository.GroupRepository;
import itmo.course.coursework.repository.GroupUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import itmo.course.coursework.service.GroupService;
import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.GroupUser;
import itmo.course.coursework.dto.request.GroupCreateRequest;
import itmo.course.coursework.dto.request.AddGroupMemberRequest;
import itmo.course.coursework.dto.request.FindAllGroupMembersRequest;
import itmo.course.coursework.dto.request.FindAllUserGroupsRequest;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.service.UserService;
import itmo.course.coursework.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final GroupUserRepository groupUserRepository;
    private final GroupRepository groupRepository;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody GroupCreateRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupUser> addMember(
            @PathVariable Long groupId,
            @RequestBody AddGroupMemberRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);

        if (!group.getCreatedBy().equals(currentUser)) {
            throw new BadRequestException("Только создатель группы может добавлять участников");
        }

        request.setGroupId(groupId);
        return ResponseEntity.ok(groupService.addGroupMember(request));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupUser>> getGroupMembers(@PathVariable Long groupId) {
        FindAllGroupMembersRequest request = new FindAllGroupMembersRequest();
        request.setGroupId(groupId);
        return ResponseEntity.ok(groupService.findAllGroupMembers(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupUser>> getUserGroups(@PathVariable Long userId) {
        FindAllUserGroupsRequest request = new FindAllUserGroupsRequest();
        request.setUserId(userId);
        return ResponseEntity.ok(groupService.findAllUserGroups(request));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.findGroupById(groupId));
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<Boolean> deleteGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.deleteGroupById(groupId));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = groupService.findGroupById(groupId);

        if (groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN , currentUser,group)) {
            throw new BadRequestException("Только администратор группы может удалять участников");
        }

        groupUserRepository.deleteGroupUserByGroupAndUser(group, currentUser);
        return ResponseEntity.noContent().build();
    }
} 