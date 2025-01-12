package itmo.course.coursework.repository;

import itmo.course.coursework.domain.Group;
import itmo.course.coursework.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByCreatedBy(User user);
}