package com.eaglebank.demo.controller.dto.transaction;

import com.eaglebank.demo.controller.dto.bankaccount.BankAccountResponseDto;
import com.eaglebank.demo.model.BankAccount;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponseDto {
        private String id;
        private BigDecimal amount;
        private String currency;
        private String type;
        private String reference;
        private String bankAccountNumber;
        private OffsetDateTime createdTimestamp;
}
