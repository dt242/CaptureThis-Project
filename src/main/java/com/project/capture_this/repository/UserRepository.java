package com.project.capture_this.repository;

import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.followers LEFT JOIN FETCH u.following WHERE u.id = :userId")
//    User findByIdWithDetails(@Param("userId") Long userId);

    Optional<User> findByUsernameOrEmail(String username, String email);

//    @Query("SELECT u from User u WHERE CONCAT(u.firstName, u.lastName) LIKE CONCAT('%', :query, '%')")
//    List<User> searchUsers(String query);

    List<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
}
