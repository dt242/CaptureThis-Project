package com.project.capture_this.service;

import com.project.capture_this.model.dto.DisplayUserDTO;
import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository,
                       UserService userService, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void likePost(Long postId) {
        User loggedUser = userService.getLoggedUser();

        if (!likeRepository.existsByPostIdAndUserId(postId, loggedUser.getId())) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

            Like like = new Like();
            like.setPost(post);
            like.setUser(loggedUser);
            likeRepository.save(like);

            if (!post.getUser().getId().equals(loggedUser.getId())) {
                notificationService.notifyLike(loggedUser, post);
            }
        }
    }

    @Transactional
    public void unlikePost(Long postId) {
        User loggedUser = userService.getLoggedUser();
        likeRepository.deleteByPostIdAndUserId(postId, loggedUser.getId());
    }

    public List<DisplayUserDTO> getUsersWhoLikedPostDTOs(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        return likes.stream()
                .map(like -> UserService.mapToDisplayUserDTO(like.getUser()))
                .collect(Collectors.toList());
    }

    public boolean isUserLikedPost(Long postId, User user) {
        return likeRepository.existsByPostIdAndUserId(postId, user.getId());
    }

    public List<Like> getLikesByPostIdSortedDesc(Long postId) {
        return likeRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    public static LikeDTO mapToLikeDTO(Like like){
        LikeDTO.LikeDTOBuilder builder = LikeDTO.builder()
                .id(like.getId())
                .createdAt(like.getCreatedAt());

        if (like.getUser() != null) {
            builder.userId(like.getUser().getId())
                    .userFirstName(like.getUser().getFirstName())
                    .userLastName(like.getUser().getLastName());
        }

        return builder.build();
    }
}