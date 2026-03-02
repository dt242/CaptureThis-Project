package com.project.capture_this.service;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;

    public PostService(PostRepository postRepository, UserService userService, CommentService commentService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    @Transactional(readOnly = true)
    public List<DisplayPostDTO> findFollowedPosts() {
        User loggedUser = userService.getLoggedUser();
        List<User> followedUsers = new ArrayList<>(loggedUser.getFollowing());
        if (followedUsers.isEmpty()) {
            return new ArrayList<>();
        }

        List<Post> posts = postRepository.findByUserInAndStatusOrderByCreatedAtDesc(followedUsers, PostStatus.PUBLISHED);
        return posts.stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisplayPostDTO> findPostsByUser(User user) {
        List<Post> posts = postRepository.findByUserAndStatusOrderByCreatedAtDesc(user, PostStatus.PUBLISHED);
        return posts.stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisplayPostDTO> findPublishedPosts() {
        User loggedUser = userService.getLoggedUser();
        return postRepository.findByUserAndStatusOrderByCreatedAtDesc(loggedUser, PostStatus.PUBLISHED)
                .stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisplayPostDTO> findDraftPostsByUser(Long userId) {
        User user = userService.findById(userId);
        return postRepository.findByUserAndStatus(user, PostStatus.DRAFT).stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void savePost(CreatePostDTO data, PostStatus status) throws IOException {
        Post post = new Post();
        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());
        post.setImage(data.getImageFile().getBytes());
        post.setUser(userService.getLoggedUser());
        post.setStatus(status);
        postRepository.save(post);
    }

    @Transactional
    public Long updatePost(EditPostDTO data, PostStatus status) throws IOException {
        Post post = postRepository.findById(data.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + data.getId()));
        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());
        if (data.hasImage()) {
            post.setImage(data.getImageFile().getBytes());
        }

        if (!post.getStatus().equals(PostStatus.PUBLISHED)) {
            post.setStatus(status);
        }
        return post.getUser().getId();
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        postRepository.delete(post);
    }

    private DisplayPostDTO mapToDisplayPostDTO(Post post) {
        return DisplayPostDTO.builder()
                .id(post.getId())
                .authorId(post.getUser() != null ? post.getUser().getId() : null)
                .authorFirstName(post.getUser() != null ? post.getUser().getFirstName() : null)
                .authorLastName(post.getUser() != null ? post.getUser().getLastName() : null)
                .description(post.getDescription())
                .title(post.getTitle())
                .comments(new HashSet<>(commentService.getCommentsByPostId(post.getId())))
                .likes(post.getLikes().stream().map(LikeService::mapToLikeDTO).collect(Collectors.toSet()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}