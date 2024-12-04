package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.GroupUser;
import itmo.course.coursework.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    List<GroupUser> findAllByGroup(Group group);

    List<GroupUser> findAllByUser(User user);

    boolean existsGroupUserByGroupAndUser(Group group, User user);
}