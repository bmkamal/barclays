package com.eaglebank.demo.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private final String accessToken;
}
