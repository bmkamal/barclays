package com.eaglebank.demo.controller.dto.bankaccount;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
public class BankAccountResponseDto{
        private String accountNumber;
        private String sortCode;
        private String name;
        private String accountType;
        private BigDecimal balance;
        String currency;
        OffsetDateTime createdTimestamp;
        OffsetDateTime updatedTimestamp;
}