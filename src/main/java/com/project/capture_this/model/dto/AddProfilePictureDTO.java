package com.project.capture_this.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddProfilePictureDTO {
    private MultipartFile profilePicture;
}
