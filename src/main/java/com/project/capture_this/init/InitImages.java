package com.project.capture_this.init;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import com.project.capture_this.repository.RoleRepository;
import com.project.capture_this.repository.UserRepository;
import com.project.capture_this.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InitImages implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;

    public InitImages(UserRepository userRepository, RoleRepository roleRepository, PostRepository postRepository, LikeRepository likeRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        List<byte[]> profilePictures = userService.getProfilePictures();
        byte[] defaultProfilePicture = userService.getDefaultProfilePicture();

        // Assign profile pictures to users
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (i < 12) {
                user.setProfilePicture(profilePictures.get(i)); // First 12 users get unique pictures
            } else if (i < 24) {
                user.setProfilePicture(profilePictures.get(i - 12)); // Next 12 users get the second set of unique pictures
            } else {
                user.setProfilePicture(defaultProfilePicture); // Remaining users get the default profile picture
            }
            userRepository.save(user); // Save user with updated profile picture
        }

        List<Post> posts = postRepository.findAll();
        List<byte[]> postPictures = userService.getPostPictures(); // Fetch 35 post pictures
        byte[] defaultPostPicture = userService.getDefaultProfilePicture(); // Optional default post picture

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            // Use modulus to cycle through the post pictures
            byte[] imageToSet = (i < 194) ? postPictures.get(i % 35) : defaultPostPicture;

            post.setImage(imageToSet);
            postRepository.save(post);
        }
    }
}
