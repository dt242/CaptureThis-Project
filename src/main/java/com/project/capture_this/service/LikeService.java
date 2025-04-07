package com.project.capture_this.service;

import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserService userService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            User user = userService.findById(userId);

            Like like = new Like();
            like.setPost(post);
            like.setUser(user);

            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);
        }
    }

    @Transactional(readOnly = true)
    public List<User> getUsersWhoLikedPost(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        return likes.stream()
                .map(Like::getUser)
                .collect(Collectors.toList());
    }

    public boolean isUserLikedPost(Long postId, User user) {
        return likeRepository.existsByPostIdAndUserId(postId, user.getId());
    }

    public List<Like> getLikesByPostIdSortedDesc(Long postId) {
        return likeRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
    }

    public static LikeDTO mapToLikeDTO(Like like){
        return LikeDTO.builder()
                .id(like.getId())
                .user(like.getUser())
                .createdAt(like.getCreatedAt())
                .build();
    }
}
