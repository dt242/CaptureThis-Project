package com.project.capture_this.service;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.repository.PostRepository;
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
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<DisplayPostDTO> findAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(post -> mapToDisplayPostDTO(post, commentService))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findFollowedPosts() {
        User loggedUser = userService.getLoggedUser();
        List<User> followedUsers = new ArrayList<>(loggedUser.getFollowing());
        List<Post> posts = postRepository.findByUserInAndStatusOrderByCreatedAtDesc(followedUsers, PostStatus.PUBLISHED);
        return posts.stream()
                .map(post -> mapToDisplayPostDTO(post, commentService))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findPostsByUser(User user) {
        List<Post> posts = postRepository.findByUserAndStatusOrderByCreatedAtDesc(user, PostStatus.PUBLISHED);
        return posts.stream()
                .map(post -> mapToDisplayPostDTO(post, commentService))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findPublishedPosts() {
        User loggedUser = userService.getLoggedUser();
        return postRepository.findByUserAndStatusOrderByCreatedAtDesc(loggedUser, PostStatus.PUBLISHED)
                .stream()
                .map(post -> mapToDisplayPostDTO(post, commentService))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findDraftPosts() {
        User loggedUser = userService.getLoggedUser();
        return postRepository.findByUserAndStatus(loggedUser, PostStatus.DRAFT).stream()
                .map(post -> mapToDisplayPostDTO(post, commentService))
                .collect(Collectors.toList());
    }

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
    public void updatePost(EditPostDTO data, PostStatus status) throws IOException {
        Post post = postRepository.findById(data.getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());
        if (data.hasImage()) {
            post.setImage(data.getImageFile().getBytes());
        }
        if (!post.getStatus().equals(PostStatus.PUBLISHED)) post.setStatus(status);
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    public static DisplayPostDTO mapToDisplayPostDTO(Post post, CommentService commentService) {
        return DisplayPostDTO.builder()
                .id(post.getId())
                .user(post.getUser())
                .image(post.getImage())
                .description(post.getDescription())
                .title(post.getTitle())
                .comments(new HashSet<>(commentService.getCommentsByPostId(post.getId())))
                .likes(post.getLikes().stream().map(LikeService::mapToLikeDTO).collect(Collectors.toSet()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
