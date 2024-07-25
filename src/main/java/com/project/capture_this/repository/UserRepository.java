package com.project.capture_this.repository;

import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their username
    Optional<User> findByUsername(String username);

    // Find a user by their email
    Optional<User> findByEmail(String email);

    // Check if a user exists by their username
    boolean existsByUsername(String username);

    // Check if a user exists by their email
    boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
}
