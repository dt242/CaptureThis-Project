package com.project.capture_this.service;

import com.project.capture_this.model.dto.CommentDTO;
import com.project.capture_this.model.entity.Post;
import com.project.capture_this.model.entity.User;
import com.project.capture_this.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PostRepository postRepository;

    private CommentService commentService;

    private MockRestServiceServer mockServer;

    private User loggedUser;
    private Post mockPost;
    private final String BASE_URL = "http://localhost:8081";

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        commentService = new CommentService(builder, BASE_URL, userService, notificationService, postRepository);
        loggedUser = new User();
        loggedUser.setId(1L);
        loggedUser.setFirstName("Daniel");
        User postOwner = new User();
        postOwner.setId(2L);
        mockPost = new Post();
        mockPost.setId(100L);
        mockPost.setUser(postOwner);
    }

    @Test
    void testGetCommentsByPostId_Success() {
        String jsonResponse = "[{\"id\":1,\"postId\":100,\"userId\":2,\"content\":\"Great post!\"}]";

        mockServer.expect(requestTo(BASE_URL + "/post/100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        List<CommentDTO> comments = commentService.getCommentsByPostId(100L);

        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals("Great post!", comments.get(0).getContent());
        mockServer.verify();
    }

    @Test
    void testGetCommentsByPostId_When4xxError_ShouldThrowEntityNotFoundException() {
        mockServer.expect(requestTo(BASE_URL + "/post/100"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.getCommentsByPostId(100L)
        );
        assertTrue(ex.getMessage().contains("Comments for post 100 not found"));
    }

    @Test
    void testGetCommentsByPostId_When5xxError_ShouldThrowIllegalStateException() {
        mockServer.expect(requestTo(BASE_URL + "/post/100"))
                .andRespond(withServerError());

        assertThrows(IllegalStateException.class, () -> commentService.getCommentsByPostId(100L));
    }

    @Test
    void testAddComment_Success_ShouldNotifyPostOwner() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(postRepository.findById(100L)).thenReturn(Optional.of(mockPost));

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        commentService.addComment(100L, "Awesome!");

        mockServer.verify();
        verify(notificationService, times(1)).notifyComment(loggedUser, mockPost);
    }

    @Test
    void testAddComment_WhenCommentingOwnPost_ShouldNotNotify() {
        mockPost.setUser(loggedUser);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(postRepository.findById(100L)).thenReturn(Optional.of(mockPost));

        mockServer.expect(requestTo(BASE_URL))
                .andRespond(withSuccess());

        commentService.addComment(100L, "My own post");

        verify(notificationService, never()).notifyComment(any(), any());
    }

    @Test
    void testAddComment_When5xxError_ShouldThrowIllegalStateException() {
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        mockServer.expect(requestTo(BASE_URL))
                .andRespond(withServerError());

        assertThrows(IllegalStateException.class, () -> commentService.addComment(100L, "Fail"));

        verify(notificationService, never()).notifyComment(any(), any());
    }

    @Test
    void testDeleteComment_Success() {
        mockServer.expect(requestTo(BASE_URL + "/50"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        commentService.deleteComment(50L);

        mockServer.verify();
    }

    @Test
    void testDeleteComment_When404_ShouldThrowEntityNotFoundException() {
        mockServer.expect(requestTo(BASE_URL + "/50"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(50L));
    }

    @Test
    void testDeleteComment_When5xxError_ShouldThrowIllegalStateException() {
        mockServer.expect(requestTo(BASE_URL + "/50"))
                .andRespond(withServerError());

        assertThrows(IllegalStateException.class, () -> commentService.deleteComment(50L));
    }
}