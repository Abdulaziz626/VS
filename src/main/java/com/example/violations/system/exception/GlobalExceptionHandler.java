package com.example.violations.system.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public void handleGlobalException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        try {
            LOGGER.error("Exception occurred during request processing: ", ex);

            // تحقق من أن الاستجابة لم تُرسل مسبقًا.
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"An unexpected error occurred. Please try again later.\"}");
                response.flushBuffer();
            } else {
                LOGGER.warn("Response was already committed, cannot write the error response.");
            }
        } catch (Exception writeException) {
            LOGGER.error("Failed to write error response to the client: ", writeException);
        }
    }
}