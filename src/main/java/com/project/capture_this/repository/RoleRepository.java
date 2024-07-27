package com.project.capture_this.repository;

import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRoles name);
    boolean existsByName(UserRoles name);
}
