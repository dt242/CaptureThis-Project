package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Favorite;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Find all favorites for a specific post
    List<Favorite> findByPost(Post post);

    // Find all favorites by a specific user
    List<Favorite> findByUser(User user);

    // Check if a user has already favorited a specific post
    Optional<Favorite> findByPostAndUser(Post post, User user);

    // Find a favorite by its ID
    Optional<Favorite> findById(Long id);
}
