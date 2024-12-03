package itmo.course.coursework.service;

import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.GroupUser;
import itmo.course.coursework.domain.GroupUserRole;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.dto.request.FindAllGroupMembersRequest;
import itmo.course.coursework.dto.request.FindAllUserGroupsRequest;
import itmo.course.coursework.dto.request.GroupCreateRequest;
import itmo.course.coursework.dto.request.AddGroupMemberRequest;
import itmo.course.coursework.repository.GroupRepository;
import itmo.course.coursework.repository.GroupUserRepository;
import itmo.course.coursework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;

    public Group findGroupById(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная группа"));
    }
    public Group createGroup(GroupCreateRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(userRepository.findByEmail(userEmail).orElseThrow());

        return groupRepository.save(group);
    }

    public GroupUser addGroupMember(AddGroupMemberRequest request) {
        GroupUserRole role = GroupUserRole.findByName(request.getUserRole());
        if (role == null)
            throw new IllegalArgumentException("Неизвестная роль пользователя в группе");
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная группа"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный пользователь"));

        GroupUser groupUser = new GroupUser();
        groupUser.setGroup(group);
        groupUser.setUser(user);
        groupUser.setRole(role);

        return groupUserRepository.save(groupUser);
    }

    public List<GroupUser> findAllGroupMembers(FindAllGroupMembersRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная группа"));

        return groupUserRepository.findAllByGroup(group);
    }

    public List<GroupUser> findAllUserGroups(FindAllUserGroupsRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный пользователь"));

        return groupUserRepository.findAllByUser(user);
    }

    public boolean existsGroupUserByGroupAndUser(Group group, User user) {
        return groupUserRepository.existsGroupUserByGroupAndUser(group, user);
    }
}
