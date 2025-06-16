package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequestDto {
    @NotBlank
    private final String email;
    @NotBlank
    private final String password;
}
