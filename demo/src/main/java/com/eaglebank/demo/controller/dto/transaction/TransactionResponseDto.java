package com.eaglebank.demo.controller.dto.transaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponseDto(
        String id,
        BigDecimal amount,
        String currency,
        String type,
        String reference,
        OffsetDateTime createdTimestamp
) {}
