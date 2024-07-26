package com.project.capture_this.service;

import com.project.capture_this.config.SecurityUtil;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.repository.PostRepository;
import com.project.capture_this.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.capture_this.service.CommentService.mapToCommentDTO;
import static com.project.capture_this.service.LikeService.mapToLikeDTO;

@Service
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    public List<DisplayPostDTO> findAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(post -> mapToDisplayPostDTO(post))
                .collect(Collectors.toList());
    }

    public List<DisplayPostDTO> findFollowedPosts() {
        return findAllPosts()
                .stream()
                .filter(post -> userRepository.findByUsername(SecurityUtil.getSessionUser())
                        .get().getFollowing().stream()
                        .equals(post.getUser()))
                .collect(Collectors.toList());
    }

    public static DisplayPostDTO mapToDisplayPostDTO(Post post) {
        return DisplayPostDTO.builder()
                .id(post.getId())
                .user(post.getUser())
                .url(post.getUrl())
                .description(post.getDescription())
                .title(post.getTitle())
                .comments(post.getComments().stream().map((comment) -> mapToCommentDTO(comment)).collect(Collectors.toSet()))
                .likes(post.getLikes().stream().map((like) -> mapToLikeDTO(like)).collect(Collectors.toSet()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
