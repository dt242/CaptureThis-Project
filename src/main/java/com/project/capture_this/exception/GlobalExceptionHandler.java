package com.project.capture_this.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return "error/404";
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public String handleNoFound404Errors(Exception ex) {
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllOtherExceptions(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred.");
        ex.printStackTrace();
        return "error/500";
    }
}