package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequestDto(
        String name,
        @Valid
        AddressDto address,
        @Email
        String email,
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
        String phoneNumber
) {}
