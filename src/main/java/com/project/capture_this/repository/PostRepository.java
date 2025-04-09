package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserAndStatus(User user, PostStatus status);
    List<Post> findByUserAndStatusOrderByCreatedAtDesc(User user, PostStatus status);
    List<Post> findByUserInAndStatusOrderByCreatedAtDesc(List<User> users, PostStatus status);

}
