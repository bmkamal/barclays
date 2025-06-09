package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(
        @NotBlank String name,
        @NotNull
        @Valid
        AddressDto address,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
        String phoneNumber,
        @NotBlank
        @Size(min = 8) String password
) {}