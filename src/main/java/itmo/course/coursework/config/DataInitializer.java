package itmo.course.coursework.config;

import itmo.course.coursework.domain.*;
import itmo.course.coursework.repository.*;
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
    private final UserRewardRepository userRewardRepository;
    private final NotificationRepository notificationRepository;

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
                createCategory("Уборка", "Задачи по уборке дома"),
                createCategory("Покупки", "Задачи по покупке продуктов и товаров"),
                createCategory("Ремонт", "Задачи по ремонту и обслуживанию дома"),
                createCategory("Дети", "Задачи, связанные с детьми")
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
                createGroup("Семья Ивановых", "Домохозяйство семьи Ивановых", admin),
                createGroup("Соседи", "Соседская группа взаимопомощи", admin)
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
                createTask("Помыть посуду", "Помыть посуду после ужина", categories.get(0), groups.get(0), 3),
                createTask("Купить молоко", "Купить молоко в магазине", categories.get(1), groups.get(0), 2),
                createTask("Починить кран", "Починить протекающий кран на кухне", categories.get(2), groups.get(0), 1),
                createTask("Погулять с собакой", "Погулять с собакой в парке", categories.get(3), groups.get(0), 2),
                createTask("Убрать в гостиной", "Пропылесосить и протереть пыль в гостиной", categories.get(0), groups.get(0), 3),
                createTask("Забрать детей из школы", "Забрать детей из школы к 15:00", categories.get(3), groups.get(0), 1)
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

            Notification notification = new Notification();
            notification.setUserTask(userTask);
            notification.setDate(LocalDateTime.now());
            notification.setTitle("ААААА!!!!");
            notification.setDescription("Погуляй уже с собакой!");
            notificationRepository.save(notification);
        }
    }

    private void createCommentsForTasks(List<Task> tasks) {
        for (Task task : tasks) {
            Comment comment = new Comment();
            comment.setComment("Не забудьте выполнить задачу: " + task.getTitle());
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
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setIsRepeated(false);
        task.setIsCompleted(false);
        return task;
    }

    private void createRewards(List<User> users) {
        List<UserReward> userRewards = Arrays.asList(
                createReward("Лучший помощник", "За выполнение всех задач в срок", users.get(0)),
                createReward("Суперпокупатель", "За своевременные покупки", users.get(1)),
                createReward("Мастер на все руки", "За ремонт и обслуживание дома", users.get(2))
        );
        userRewardRepository.saveAll(userRewards);
    }

    private UserReward createReward(String name, String description, User user) {
        Reward reward = new Reward();
        reward.setName(name);
        reward.setDescription(description);
        rewardRepository.save(reward);

        UserReward userReward = new UserReward();
        userReward.setReward(reward);
        userReward.setAwardedDate(LocalDateTime.now());
        userReward.setUser(user);

        return userReward;
    }
} 