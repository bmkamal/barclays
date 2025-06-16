package com.eaglebank.demo.controller.dto;

import lombok.*;

import java.time.LocalDateTime;


    @Builder
    @Getter
    @AllArgsConstructor
    public class ErrorResponse {
        private final String message;
}

