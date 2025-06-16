package com.eaglebank.demo.model;

import com.eaglebank.demo.controller.dto.transaction.TransactionResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "bankAccount")
@EqualsAndHashCode(exclude = "bankAccount")
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal amount;
    private String type;
    private String currency;
    private String reference;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number")
    private BankAccount bankAccount;
    private OffsetDateTime createdTimestamp;

    public TransactionResponseDto toResponseDto() {
        return TransactionResponseDto.builder()
                .id(this.getId())
                .amount(this.getAmount())
                .type(this.getType())
                .currency(this.getCurrency())
                .bankAccountNumber(this.getBankAccount().getAccountNumber())
                .reference(this.getReference())
                .createdTimestamp(this.getCreatedTimestamp())
                .build();
    }
}
