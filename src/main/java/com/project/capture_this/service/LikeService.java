package com.project.capture_this.service;

import com.project.capture_this.model.dto.LikeDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import com.project.capture_this.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {

            Post postProxy = postRepository.getReferenceById(postId);
            User userProxy = userRepository.getReferenceById(userId);

            Like like = new Like();
            like.setPost(postProxy);
            like.setUser(userProxy);

            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
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