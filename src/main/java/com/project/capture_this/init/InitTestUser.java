package com.project.capture_this.init;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.Gender;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.model.enums.UserRoles;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import com.project.capture_this.repository.RoleRepository;
import com.project.capture_this.repository.UserRepository;
import com.project.capture_this.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class InitTestUser implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;


    public InitTestUser(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, PostRepository postRepository, LikeRepository likeRepository, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        Role userRole = roleRepository.findByName(UserRoles.USER).orElseThrow();

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setBio("Coffee. Code. Repeat.");
        user.setRoles(Set.of(userRole));
        user.setGender(Gender.MALE);
        user.setBirthDate(LocalDate.of(1990, 11, 15));
        user.setProfilePicture(userService.getProfilePictures().get(3));
        userRepository.save(user);

        List<Post> posts = new ArrayList<>();
        posts.add(createPost(user, "Exploring the World of Coding", "A journey through algorithms, data structures, and more.", LocalDateTime.of(2025, 4, 10, 10, 0), PostStatus.PUBLISHED));
        posts.add(createPost(user, "The Art of Coffee Brewing", "Mastering the techniques for the perfect cup of coffee.", LocalDateTime.of(2025, 4, 10, 12, 0), PostStatus.PUBLISHED));
        posts.add(createPost(user, "My Favorite Programming Languages", "A list of programming languages I love to work with.", LocalDateTime.of(2025, 4, 10, 14, 0), PostStatus.PUBLISHED));
        posts.add(createPost(user, "Life as a Developer", "A reflection on the challenges and rewards of being a developer.", LocalDateTime.of(2025, 4, 10, 16, 0), PostStatus.PUBLISHED));
        posts.add(createPost(user, "Life as a Developer", "Not finished", LocalDateTime.of(2025, 4, 10, 16, 0), PostStatus.DRAFT));
        posts.add(createPost(user, "Tech Meetups and Networking", "The importance of attending tech meetups and building a network.", LocalDateTime.of(2025, 4, 10, 18, 0), PostStatus.PUBLISHED));
        posts.add(createPost(user, "Tech Meetups and Networking", "Not finished either", LocalDateTime.of(2025, 4, 10, 18, 0), PostStatus.DRAFT));

        List<byte[]> postPictures = userService.getPostPictures();
        byte[] defaultPostPicture = userService.getDefaultProfilePicture();

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            byte[] imageToSet = (i < 7) ? postPictures.get(i % 35) : defaultPostPicture;

            post.setImage(imageToSet);
            postRepository.save(post);
        }

        Role adminRole = roleRepository.findByName(UserRoles.ADMIN).orElseThrow();

        User admin = new User();
        admin.setFirstName("Test");
        admin.setLastName("Admin");
        admin.setUsername("testadmin");
        admin.setEmail("testadmin@example.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setBio("Coffee. Code. Repeat.");
        admin.setRoles(Set.of(adminRole));
        admin.setGender(Gender.MALE);
        admin.setBirthDate(LocalDate.of(1990, 11, 15));
        admin.setProfilePicture(userService.getProfilePictures().get(3));
        userRepository.save(admin);

        List<Post> postsAdmin = new ArrayList<>();
        postsAdmin.add(createPost(admin, "Exploring the World of Coding", "A journey through algorithms, data structures, and more.", LocalDateTime.of(2025, 4, 10, 10, 0), PostStatus.PUBLISHED));
        postsAdmin.add(createPost(admin, "The Art of Coffee Brewing", "Mastering the techniques for the perfect cup of coffee.", LocalDateTime.of(2025, 4, 10, 12, 0), PostStatus.PUBLISHED));
        postsAdmin.add(createPost(admin, "My Favorite Programming Languages", "A list of programming languages I love to work with.", LocalDateTime.of(2025, 4, 10, 14, 0), PostStatus.PUBLISHED));
        postsAdmin.add(createPost(admin, "Life as a Developer", "A reflection on the challenges and rewards of being a developer.", LocalDateTime.of(2025, 4, 10, 16, 0), PostStatus.PUBLISHED));
        postsAdmin.add(createPost(admin, "Life as a Developer", "Not finished", LocalDateTime.of(2025, 4, 10, 16, 0), PostStatus.DRAFT));
        postsAdmin.add(createPost(admin, "Tech Meetups and Networking", "The importance of attending tech meetups and building a network.", LocalDateTime.of(2025, 4, 10, 18, 0), PostStatus.PUBLISHED));
        postsAdmin.add(createPost(admin, "Tech Meetups and Networking", "Not finished either", LocalDateTime.of(2025, 4, 10, 18, 0), PostStatus.DRAFT));

        for (int i = 0; i < postsAdmin.size(); i++) {
            Post post = postsAdmin.get(i);

            byte[] imageToSet = (i < 7) ? postPictures.get(i % 35) : defaultPostPicture;

            post.setImage(imageToSet);
            postRepository.save(post);
        }
    }

    private Post createPost(User user, String title, String description, LocalDateTime createdAt, PostStatus status) {
        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setDescription(description);
        post.setStatus(status);
        post.setCreatedAt(createdAt);
        post.setUpdatedAt(createdAt);
        return post;
    }
}