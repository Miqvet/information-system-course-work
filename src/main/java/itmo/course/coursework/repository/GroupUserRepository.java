package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.GroupUser;
import itmo.course.coursework.domain.GroupUserRole;
import itmo.course.coursework.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    List<GroupUser> findAllByGroup(Group group);

    List<GroupUser> findAllByUser(User user);

    boolean existsByRoleAndUserAndGroup(GroupUserRole groupUserRole, User user, Group group);

    boolean deleteGroupUserByGroupAndUser(Group group, User user);

    boolean deleteGroupUserByGroupAndUserId(Group group, Long userId);

    boolean existsByRoleAndUserAndGroupId(GroupUserRole groupUserRole, User user, Long groupId);

    boolean existsGroupUserByGroupAndUser(Group group, User user);

    boolean existsGroupUserByGroupId(Long groupId);
}