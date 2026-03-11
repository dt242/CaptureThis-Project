package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostService(PostRepository postRepository, UserService userService, CommentService commentService, LikeService likeService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    public byte[] findImageByPostId(Long postId) {
        return findById(postId).getImage();
    }

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

    public List<DisplayPostDTO> findPostsByUser(User user) {
        List<Post> posts = postRepository.findByUserAndStatusOrderByCreatedAtDesc(user, PostStatus.PUBLISHED);
        return posts.stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    public List<DisplayPostDTO> findPublishedPosts() {
        User loggedUser = userService.getLoggedUser();
        return postRepository.findByUserAndStatusOrderByCreatedAtDesc(loggedUser, PostStatus.PUBLISHED)
                .stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    public List<DisplayPostDTO> findDraftPostsByUser(Long userId) {
        User loggedUser = userService.getLoggedUser();
        if (!loggedUser.getId().equals(userId) && !loggedUser.isAdmin()) {
            throw new EntityNotFoundException("Drafts not found.");
        }
        User user = userService.findById(userId);
        return postRepository.findByUserAndStatus(user, PostStatus.DRAFT).stream()
                .map(this::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    public PostStatus getPostStatus(String action) {
        return "post".equals(action) ? PostStatus.PUBLISHED : PostStatus.DRAFT;
    }

    private void checkPostOwnership(Post post) {
        User loggedUser = userService.getLoggedUser();
        if (!post.getUser().getId().equals(loggedUser.getId()) && !loggedUser.isAdmin()) {
            throw new SecurityException("You do not have permission to modify or view this private post.");
        }
    }

    public EditPostDTO getPostForEditing(Long postId) {
        Post post = findById(postId);
        checkPostOwnership(post);
        return EditPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .build();
    }

    public boolean isPostStatusPublished(Long postId) {
        Post post = findById(postId);
        return post.getStatus().equals(PostStatus.PUBLISHED);
    }

    public Map<String, Object> getFullPostDetails(Long postId) {
        Post post = this.findById(postId);
        User loggedUser = userService.getLoggedUser();

        List<CommentDTO> comments = commentService.getCommentsByPostId(postId).stream()
                .sorted(Comparator.comparing(CommentDTO::getCreatedAt).reversed())
                .peek(comment -> comment.setOwnComment(comment.getUserId().equals(loggedUser.getId())))
                .toList();

        Map<Long, List<User>> commentAuthors = comments.stream()
                .map(comment -> userService.findById(comment.getUserId()))
                .collect(Collectors.groupingBy(User::getId));

        List<Like> likes = likeService.getLikesByPostIdSortedDesc(postId);
        boolean isLiked = likeService.isUserLikedPost(postId, loggedUser);

        return Map.of(
                "post", post,
                "comments", comments,
                "commentAuthors", commentAuthors,
                "likes", likes,
                "isLiked", isLiked,
                "isAdmin", loggedUser.isAdmin()
        );
    }

    @Transactional
    public void savePost(CreatePostDTO data, String action) throws IOException {
        Post post = new Post();
        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());
        post.setImage(data.getImageFile().getBytes());
        post.setUser(userService.getLoggedUser());
        post.setStatus(getPostStatus(action));
        postRepository.save(post);
    }

    @Transactional
    public Long updatePost(EditPostDTO data, String action) throws IOException {
        Post post = postRepository.findById(data.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + data.getId()));
        checkPostOwnership(post);
        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());
        if (data.hasImage()) {
            post.setImage(data.getImageFile().getBytes());
        }

        if (!post.getStatus().equals(PostStatus.PUBLISHED)) {
            post.setStatus(getPostStatus(action));
        }
        return post.getUser().getId();
    }

    @Transactional
    public Long deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
        checkPostOwnership(post);
        postRepository.delete(post);
        return post.getUser().getId();
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