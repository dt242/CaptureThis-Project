package com.project.capture_this.service;

import com.project.capture_this.model.dto.CreatePostDTO;
import com.project.capture_this.model.dto.DisplayPostDTO;
import com.project.capture_this.model.dto.EditPostDTO;
import com.project.capture_this.model.entity.Favorite;
import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import com.project.capture_this.repository.FavoriteRepository;
import com.project.capture_this.repository.LikeRepository;
import com.project.capture_this.repository.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final FavoriteRepository favoriteRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserService userService, FavoriteRepository favoriteRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.favoriteRepository = favoriteRepository;
        this.likeRepository = likeRepository;
    }

    public List<DisplayPostDTO> findAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(PostService::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findFollowedPosts() {
        User loggedUser = userService.getLoggedUser();
        return findAllPosts()
                .stream()
                .filter(post -> loggedUser.getFollowing().contains(post.getUser()))
                .collect(Collectors.toList());
    }

    public static DisplayPostDTO mapToDisplayPostDTO(Post post) {
        return DisplayPostDTO.builder()
                .id(post.getId())
                .user(post.getUser())
                .image(post.getImage())
                .description(post.getDescription())
                .title(post.getTitle())
                .comments(post.getComments().stream().map(CommentService::mapToCommentDTO).collect(Collectors.toSet()))
                .likes(post.getLikes().stream().map(LikeService::mapToLikeDTO).collect(Collectors.toSet()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public void savePost(CreatePostDTO data, PostStatus status) throws IOException {
        Post post = new Post();
        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());
        post.setImage(data.getImageFile().getBytes());
        post.setUser(userService.getLoggedUser());
        post.setStatus(status); // Set status based on button action

        postRepository.save(post);
    }

    @Transactional
    public List<DisplayPostDTO> findPostsByUser(User user) {
        List<Post> posts = postRepository.findByUser(user);
        return posts.stream()
                .map(PostService::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findPublishedPosts() {
        User loggedUser = userService.getLoggedUser();
        return postRepository.findByUserAndStatus(loggedUser, PostStatus.PUBLISHED).stream()
                .map(PostService::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DisplayPostDTO> findDraftPosts() {
        User loggedUser = userService.getLoggedUser();
        return postRepository.findByUserAndStatus(loggedUser, PostStatus.DRAFT).stream()
                .map(PostService::mapToDisplayPostDTO)
                .collect(Collectors.toList());
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public List<DisplayPostDTO> findFavoritePosts() {
        User loggedUser = userService.getLoggedUser();
        List<Favorite> favorites = favoriteRepository.findByUser(loggedUser);

        return favorites.stream()
                .map(favorite -> postRepository.findById(favorite.getPost().getId())
                        .map(PostService::mapToDisplayPostDTO)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePost(EditPostDTO data) throws IOException {
        Post post = postRepository.findById(data.getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(data.getTitle());
        post.setDescription(data.getDescription());

        if (data.hasImage()) {
            post.setImage(data.getImageFile().getBytes());
        }

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.delete(post);
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
}
