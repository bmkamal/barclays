package com.eaglebank.demo.controller.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransactionRequestDto(
        @NotNull
        @Positive
        BigDecimal amount,
        @NotBlank
        String currency,
        @NotBlank
        String type,
        String reference
) {}
