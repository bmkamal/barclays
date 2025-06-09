package com.eaglebank.demo.controller.dto.bankaccount;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record BankAccountResponseDto(
        String accountNumber,
        String sortCode,
        String name,
        String accountType,
        BigDecimal balance,
        String currency,
        OffsetDateTime createdTimestamp,
        OffsetDateTime updatedTimestamp
) {}