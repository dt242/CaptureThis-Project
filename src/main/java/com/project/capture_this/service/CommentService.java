package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class CommentService {

    private final RestClient restClient;
    public CommentService(RestClient.Builder restClientBuilder, @Value("${app.services.comments.url}") String commentsServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(commentsServiceUrl).build();
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return restClient
                .get()
                .uri("/post/{postId}", postId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void addComment(CommentDTO commentDTO) {
        restClient
                .post()
                .body(commentDTO)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteComment(Long commentId) {
        restClient
                .delete()
                .uri("/{commentId}", commentId)
                .retrieve()
                .toBodilessEntity();
    }
}