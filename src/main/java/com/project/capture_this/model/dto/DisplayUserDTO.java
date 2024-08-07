package com.project.capture_this.model.dto;

import com.project.capture_this.model.entity.Role;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Set<Role> roles;
    private LocalDate birthDate;
    private Gender gender;
    private byte[] profilePicture;
    private String bio;
    private Set<User> followers;
    private Set<User> following;
    private LocalDateTime createdAt;

    @Builder
    public DisplayUserDTO(Long id, String firstName, String lastName, byte[] profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
    }
}
