package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Comment;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by the user
    List<Post> findByUser(User user);

    // Find a post by its title
    Optional<Post> findByTitle(String title);

    // Find all posts with a specific status
    List<Post> findByStatus(PostStatus status);

    // Find posts by URL (assuming URL is unique or should be unique)
    Optional<Post> findByUrl(String url);

    // Find posts with a specific description (could use like query or exact match depending on requirements)
    List<Post> findByDescriptionContaining(String description);

    // Find all posts that have a specific comment
    List<Post> findByCommentsContaining(Comment comment);

    // Find all posts liked by a specific user
    List<Post> findByLikesUser(User user);

    // Find all posts marked as favorites by a specific user
    List<Post> findByFavoritesUser(User user);
}
