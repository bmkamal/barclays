package com.eaglebank.demo.model;

import com.eaglebank.demo.controller.dto.bankaccount.BankAccountResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "transactions"})
@EqualsAndHashCode(exclude = {"user", "transactions"})
@Entity
public class BankAccount {
    @Id
    private String accountNumber;
    private String name;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String sortCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
    private OffsetDateTime createdTimestamp;
    private OffsetDateTime updatedTimestamp;

    public  BankAccountResponseDto toResponseDto() {
        return BankAccountResponseDto.builder()
                .accountNumber(this.getAccountNumber())
                .name(this.getName())
                .accountType(this.getAccountType())
                .balance(this.getBalance())
                .currency(this.getCurrency())
                .sortCode(this.getSortCode())
                .createdTimestamp(this.getCreatedTimestamp())
                .updatedTimestamp(this.getUpdatedTimestamp())
                .build();
    }
}
