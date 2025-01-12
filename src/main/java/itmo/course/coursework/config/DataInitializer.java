package itmo.course.coursework.config;

import itmo.course.coursework.domain.*;
import itmo.course.coursework.repository.*;
import itmo.course.coursework.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserTaskRepository userTaskRepository;
    private final RewardRepository rewardRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        List<User> users = createUsers();
        List<Category> categories = createCategories();
        List<Group> groups = createGroups(users.get(0));
        addUsersToGroups(users, groups);
        createTasks(categories, groups, users);
        createRewards(users);
    }

    private List<User> createUsers() {
        List<User> users = Arrays.asList(
            createUser("john@example.com", "John", "Doe"),
            createUser("jane@example.com", "Jane", "Smith"),
            createUser("bob@example.com", "Bob", "Johnson"),
            createUser("alice@example.com", "Alice", "Brown")
        );
        return userRepository.saveAll(users);
    }

    private User createUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    private List<Category> createCategories() {
        List<Category> categories = Arrays.asList(
            createCategory("Работа", "Рабочие задачи"),
            createCategory("Учеба", "Учебные задания"),
            createCategory("Личное", "Личные дела")
        );
        return categoryRepository.saveAll(categories);
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private List<Group> createGroups(User admin) {
        List<Group> groups = Arrays.asList(
            createGroup("Команда разработки", "Группа разработчиков проекта", admin),
            createGroup("Учебная группа", "Группа для учебных заданий", admin)
        );
        return groupRepository.saveAll(groups);
    }

    private Group createGroup(String name, String description, User createdBy) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(createdBy);
        return group;
    }

    private void addUsersToGroups(List<User> users, List<Group> groups) {
        for (Group group : groups) {
            for (User user : users) {
                GroupUser groupUser = new GroupUser();
                groupUser.setGroup(group);
                groupUser.setUser(user);
                groupUser.setRole(user.equals(group.getCreatedBy()) ? 
                    GroupUserRole.ADMIN : GroupUserRole.MEMBER);
                groupUserRepository.save(groupUser);
            }
        }
    }

    private void createTasks(List<Category> categories, List<Group> groups, List<User> users) {
        List<Task> tasks = Arrays.asList(
            createTask("Разработка API", "Разработать REST API", categories.get(0), groups.get(0), 3),
            createTask("Написать тесты", "Написать unit-тесты", categories.get(0), groups.get(0), 2),
            createTask("Подготовить презентацию", "Подготовить презентацию проекта", categories.get(1), groups.get(1), 2),
            createTask("Изучить Spring Security", "Изучить основы Spring Security", categories.get(1), groups.get(1), 1),
            createTask("Код ревью", "Провести код ревью", categories.get(0), groups.get(0), 3),
            createTask("Документация", "Написать документацию", categories.get(0), groups.get(0), 1)
        );

        tasks = taskRepository.saveAll(tasks);
        createCommentsForTasks(tasks);
        for (int i = 0; i < tasks.size(); i++) {
            UserTask userTask = new UserTask();
            userTask.setTask(tasks.get(i));
            userTask.setUser(users.get(i % users.size()));
            userTask.setAssignedDate(LocalDateTime.now());
            userTask.setCompletionStatus(false);
            userTaskRepository.save(userTask);
        }
    }

    private void createCommentsForTasks(List<Task> tasks) {
        for (Task task : tasks) {
            Comment comment = new Comment();
            comment.setComment("this is stupid to do this task №" + task.getId());
            comment.setTask(task);
            Group group = groupRepository.findById(task.getGroup().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Неизвестная группа"));
            List<GroupUser> groupUsers = groupUserRepository.findAllByGroup(group);
            comment.setUser(groupUsers.get(0).getUser());
            commentRepository.save(comment);
        }
    }

    private Task createTask(String title, String description, Category category, Group group, int priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCategory(category);
        task.setGroup(group);
        task.setCurrentPriority(priority);
        task.setDeadline(LocalDateTime.now().plusDays(7));
        task.setIsRepeated(false);
        task.setIsCompleted(false);
        return task;
    }

    private void createRewards(List<User> users) {
        List<Reward> rewards = Arrays.asList(
            createReward("Лучший исполнитель", "За выполнение всех задач в срок", users.get(0)),
            createReward("Командный игрок", "За помощь коллегам", users.get(1)),
            createReward("Новатор", "За инновационные решения", users.get(2))
        );
        rewardRepository.saveAll(rewards);
    }

    private Reward createReward(String name, String description, User user) {
        Reward reward = new Reward();
        reward.setName(name);
        reward.setDescription(description);
        reward.setUser(user);
        return reward;
    }
} 