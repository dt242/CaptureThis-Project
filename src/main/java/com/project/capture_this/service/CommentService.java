package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Arrays;

@Service
public class CommentService {

    private final RestClient restClient;

    private final String COMMENTS_SERVICE_URL = "http://localhost:8081/api/comments";

    @Autowired
    public CommentService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl(COMMENTS_SERVICE_URL).build();
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        CommentDTO[] comments = restClient
                .get()
                .uri("/post/{postId}", postId)
                .retrieve()
                .body(CommentDTO[].class);

        return Arrays.asList(comments);
    }

    public CommentDTO addComment(CommentDTO commentDTO) {
        return restClient
                .post()
                .uri("")
                .body(commentDTO)
                .retrieve()
                .body(CommentDTO.class);
    }

    public void deleteComment(Long commentId) {
        restClient
                .delete()
                .uri("/{commentId}", commentId)
                .retrieve();
    }
}
