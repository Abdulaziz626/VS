package com.example.violations.system.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, HttpServletRequest request) {

ex.printStackTrace();
        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Setter
    @Getter
    static class ErrorResponse {
        // Getters and Setters ضروريّة للعمل
        private int statusCode;
        private String message;
        private String path;

        public ErrorResponse(int statusCode, String message, String path) {
            this.statusCode = statusCode;
            this.message = message;
            this.path = path;
        }

    }
}