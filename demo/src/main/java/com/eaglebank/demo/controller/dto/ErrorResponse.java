package com.eaglebank.demo.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();
    private String exception;
    private String message;
    private String path;
}

