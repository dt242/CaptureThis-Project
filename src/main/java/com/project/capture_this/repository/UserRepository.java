package com.project.capture_this.repository;

import com.project.capture_this.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserIds();
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);

    List<User> findByUpdatedAtBeforeAndIsActiveTrue(LocalDateTime lastActiveBefore);
}
