package itmo.course.coursework.service;

import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.GroupUser;
import itmo.course.coursework.domain.GroupUserRole;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.dto.request.FindAllGroupMembersRequest;
import itmo.course.coursework.dto.request.FindAllUserGroupsRequest;
import itmo.course.coursework.dto.request.GroupCreateRequest;
import itmo.course.coursework.dto.request.AddGroupMemberRequest;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.repository.GroupRepository;
import itmo.course.coursework.repository.GroupUserRepository;
import itmo.course.coursework.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public Group findGroupById(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная группа"));
    }
    public Group createGroup(GroupCreateRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(user);
        
        // Сохраняем группу
        group = groupRepository.save(group);
        
        // Создаем связь GroupUser с ролью ADMIN
        GroupUser groupUser = new GroupUser();
        groupUser.setGroup(group);
        groupUser.setUser(user);
        groupUser.setRole(GroupUserRole.ADMIN);
        groupUserRepository.save(groupUser);

        return group;
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

    public List<Group> findAllUserGroups(FindAllUserGroupsRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный пользователь"));
        return groupUserRepository.findAllByUser(user).stream().map(GroupUser::getGroup).collect(Collectors.toList());
    }

    public boolean isUserInGroup(Group group, User user) {
        return groupUserRepository.existsGroupUserByGroupAndUser(group, user);
    }

    public boolean deleteGroupById(Long groupId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        if (groupUserRepository.existsByRoleAndUserAndGroupId(GroupUserRole.ADMIN, user, groupId))
            throw new IllegalArgumentException("Вы не админ!!!!!");
        if (groupUserRepository.existsGroupUserByGroupId(groupId))
            throw new IllegalArgumentException("В группе остались пользователи");

        groupUserRepository.deleteById(groupId);
        return true;
    }
    public boolean deleteMember(Long groupId, Long userId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(userEmail);
        Group group = findGroupById(groupId);

        if (groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN, currentUser, group)) {
            throw new BadRequestException("Только администратор группы может удалять участников");
        }

        return groupUserRepository.deleteGroupUserByGroupAndUserId(group, userId);
    }

    public boolean isUserAdmin(Group group, User user) {
        return groupUserRepository.existsByRoleAndUserAndGroup(GroupUserRole.ADMIN, user, group);
    }

    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }
}
