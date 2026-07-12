package com.example.docmate.global.response;

import com.example.docmate.global.exception.GlobalException;

public class GlobalResponseBuilder {

    public static GlobalResponse buildSuccessResponse(String message) {
        return GlobalResponse.builder()
                .status(true)
                .message(message)
                .build();
    }

    public static GlobalResponse buildSuccessResponseWithData(String message, Object data) {

        return GlobalResponse.builder()
                .status(true)
                .message(message)
                .data(data)
                .build();
    }

    public static GlobalResponse buildErrorResponse(GlobalException e) {
        return GlobalResponse.builder()
                .status(false)
                .message(e.getMessage())
                .build();
    }
}
