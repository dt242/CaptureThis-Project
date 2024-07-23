package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Comment;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for a specific post
    List<Comment> findByPost(Post post);

    // Find all comments made by a specific user
    List<Comment> findByUser(User user);

    // Find a comment by its content (or part of the content)
    List<Comment> findByContentContaining(String content);

    // Find all comments marked as deleted
    List<Comment> findByIsDeleted(boolean isDeleted);

    // Find a comment by its ID, if it's not deleted
    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    // Find all comments for a specific post that are not deleted
    List<Comment> findByPostAndIsDeletedFalse(Post post);

    // Find all comments made by a specific user that are not deleted
    List<Comment> findByUserAndIsDeletedFalse(User user);
}
