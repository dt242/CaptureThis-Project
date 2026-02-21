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


        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (i < 12) {
                user.setProfilePicture(profilePictures.get(i));
            } else if (i < 24) {
                user.setProfilePicture(profilePictures.get(i - 12));
            } else {
                user.setProfilePicture(defaultProfilePicture);
            }
            userRepository.save(user);
        }

        List<Post> posts = postRepository.findAll();
        List<byte[]> postPictures = userService.getPostPictures();
        byte[] defaultPostPicture = userService.getDefaultProfilePicture();

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            byte[] imageToSet = (i < 194) ? postPictures.get(i % 35) : defaultPostPicture;

            post.setImage(imageToSet);
            postRepository.save(post);
        }
    }
}
