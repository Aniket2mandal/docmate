package com.example.docmate.global.exception;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalResponse> handleGlobalException(GlobalException e) {
        GlobalResponse response = GlobalResponseBuilder.buildErrorResponse(e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(response);
    }
}
