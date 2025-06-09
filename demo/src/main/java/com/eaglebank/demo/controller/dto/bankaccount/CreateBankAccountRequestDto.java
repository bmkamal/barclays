package com.eaglebank.demo.controller.dto.bankaccount;

import jakarta.validation.constraints.NotBlank;

public record CreateBankAccountRequestDto(
        @NotBlank String name,
        @NotBlank String accountType
) {}
