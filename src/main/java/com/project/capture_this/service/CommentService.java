package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
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
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new EntityNotFoundException("Comments for post " + postId + " not found");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new IllegalStateException("Comments microservice is currently unavailable");
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    public void addComment(CommentDTO commentDTO) {
        restClient
                .post()
                .body(commentDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new IllegalStateException("Could not add comment. Service unavailable.");
                })
                .toBodilessEntity();
    }

    public void deleteComment(Long commentId) {
        restClient
                .delete()
                .uri("/{commentId}", commentId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new EntityNotFoundException("Comment with ID " + commentId + " not found");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new IllegalStateException("Comments microservice is currently unavailable");
                })
                .toBodilessEntity();
    }
}