package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CommentService {

    public static CommentDTO mapToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.isDeleted())
                .build();
    }

    @Autowired
    private RestTemplate restTemplate;

    private final String COMMENTS_SERVICE_URL = "http://localhost:8081/api/comments";

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        String url = COMMENTS_SERVICE_URL + "/post/" + postId;
        CommentDTO[] comments = restTemplate.getForObject(url, CommentDTO[].class);
        return List.of(comments);
    }

    public CommentDTO addComment(CommentDTO commentDTO) {
        return restTemplate.postForObject(COMMENTS_SERVICE_URL, commentDTO, CommentDTO.class);
    }

    public CommentDTO updateComment(Long commentId, String content) {
        String url = COMMENTS_SERVICE_URL + "/" + commentId;
        restTemplate.put(url, content);
        return restTemplate.getForObject(url, CommentDTO.class);
    }

    public void deleteComment(Long commentId) {
        String url = COMMENTS_SERVICE_URL + "/" + commentId;
        restTemplate.delete(url);
    }
}
