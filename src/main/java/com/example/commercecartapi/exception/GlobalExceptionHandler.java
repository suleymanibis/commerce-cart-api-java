package com.example.commercecartapi.exception;

import com.example.commercecartapi.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        String message = "An error occurred: " + ex.getMessage();
        String path =request.getRequestURI();

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                message,
                path
        );
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllJavaExceptions(Exception ex, HttpServletRequest request){
        String message = "An unexpected error occurred.";
        String path = request.getRequestURI();

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                path
        );
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequests(MethodArgumentNotValidException ex, HttpServletRequest request){
        String message = "Input is not valid.";
        String path = request.getRequestURI();

        ApiErrorResponse  apiErrorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message,
                path
        );
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserExistsException(UserAlreadyExistsException ex, HttpServletRequest request){
        String message = "User is already exists.";
        String path = request.getRequestURI();

        ApiErrorResponse  apiErrorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                message,
                path
        );
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiErrorResponse);
    }

    @ExceptionHandler(NotActiveProductException.class)
    public ResponseEntity<ApiErrorResponse> handleNotActiveProductException(NotActiveProductException ex, HttpServletRequest request){
        String message = "Product status is not active!";
        String path = request.getRequestURI();

        ApiErrorResponse  apiErrorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message,
                path
        );
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ApiErrorResponse> handleEmptyCartException(EmptyCartException ex, HttpServletRequest request){
        String message = "Cart is empty!";
        String path = request.getRequestURI();

        ApiErrorResponse  apiErrorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                message,
                path
        );
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(apiErrorResponse);
    }
}
