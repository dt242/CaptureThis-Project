package com.project.capture_this.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private Model model;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleEntityNotFound_ShouldReturn404AndSetErrorMessage() {
        String errorMessage = "User not found with ID: 1";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);
        String viewName = globalExceptionHandler.handleEntityNotFound(exception, model);

        assertEquals("error/404", viewName);
        verify(model, times(1)).addAttribute("errorMessage", errorMessage);
    }

    @Test
    void testHandleRouting404Errors_ShouldReturn404() {
        String viewName = globalExceptionHandler.handleRouting404Errors();

        assertEquals("error/404", viewName);
    }

    @Test
    void testHandleAllOtherExceptions_ShouldReturn500AndSetDefaultMessage() {
        Exception exception = new RuntimeException("Database exploded!");
        String viewName = globalExceptionHandler.handleAllOtherExceptions(exception, model);

        assertEquals("error/500", viewName);
        verify(model, times(1)).addAttribute("errorMessage", "An unexpected error occurred.");
    }
}