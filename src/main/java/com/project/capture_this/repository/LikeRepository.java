package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Like;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Find all likes for a specific post
    List<Like> findByPost(Post post);

    // Find all likes by a specific user
    List<Like> findByUser(User user);

    // Check if a user has already liked a specific post
    Optional<Like> findByPostAndUser(Post post, User user);

    // Find a like by its ID
    Optional<Like> findById(Long id);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);

    List<Like> findByPostId(Long postId);
}
