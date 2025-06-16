// Place this file in: src/test/java/com/eaglebank/demo/service/TransactionServiceTest.java
package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.transaction.CreateTransactionRequestDto;
import com.eaglebank.demo.controller.dto.transaction.TransactionResponseDto;
import com.eaglebank.demo.exception.InvalidOperationException;
import com.eaglebank.demo.exception.NotFoundException;
import com.eaglebank.demo.model.BankAccount;
import com.eaglebank.demo.model.Transaction;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionService transactionService;

    private BankAccount bankAccount;
    private String userId;
    private String accountNumber;
    private String transactionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        accountNumber = "12345678";
        transactionId = UUID.randomUUID().toString();

        User user = User.builder().id(userId).build();
        bankAccount = BankAccount.builder()
                .accountNumber(accountNumber)
                .balance(new BigDecimal("1000.00")) // Start with a balance for withdrawal tests
                .user(user)
                .build();
    }

    // =================================================================================
    // Tests for createTransaction()
    // =================================================================================

    @Test
    @DisplayName("createTransaction (Deposit) should increase balance and save transaction")
    void createTransaction_whenDeposit_shouldSucceed() {
        CreateTransactionRequestDto depositRequest = new CreateTransactionRequestDto(new BigDecimal("200.50"), "GBP", "deposit", "Salary");
        when(bankAccountService.findAccountAndVerifyOwnership(accountNumber, userId)).thenReturn(bankAccount);

        Transaction savedTransactionMock = mock(Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransactionMock);
        when(savedTransactionMock.toResponseDto()).thenReturn(
                TransactionResponseDto.builder()
                        .id(transactionId)
                        .amount(new BigDecimal("200.50"))
                        .currency("GBP")
                        .type("deposit")
                        .reference("Salary")
                        .createdTimestamp(null)
                        .build()
        );

        TransactionResponseDto result = transactionService.createTransaction(accountNumber, depositRequest, userId);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.50"), result.getAmount());

        ArgumentCaptor<BankAccount> accountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountService, times(1)).findAccountAndVerifyOwnership(accountNumber, userId);

        assertEquals(new BigDecimal("1200.50"), bankAccount.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("createTransaction (Withdrawal) should decrease balance and save transaction")
    void createTransaction_whenWithdrawalAndSufficientFunds_shouldSucceed() {
        CreateTransactionRequestDto withdrawalRequest = new CreateTransactionRequestDto(new BigDecimal("100.00"), "GBP", "withdrawal", "ATM");
        when(bankAccountService.findAccountAndVerifyOwnership(accountNumber, userId)).thenReturn(bankAccount);

        Transaction savedTransactionMock = mock(Transaction.class);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransactionMock);
        when(savedTransactionMock.toResponseDto()).thenReturn(
                TransactionResponseDto.builder()
                        .id(transactionId)
                        .amount(new BigDecimal("100.00"))
                        .currency("GBP")
                        .type("withdrawal")
                        .reference("mortgage payment")
                        .createdTimestamp(null)
                        .build()
        );
        TransactionResponseDto result = transactionService.createTransaction(accountNumber, withdrawalRequest, userId);

        assertNotNull(result);
        assertEquals(new BigDecimal("900.00"), bankAccount.getBalance()); // 1000 - 100
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("createTransaction should throw InvalidOperationException for insufficient funds")
    void createTransaction_whenInsufficientFunds_shouldThrowException() {
        CreateTransactionRequestDto withdrawalRequest = new CreateTransactionRequestDto(new BigDecimal("2000.00"), "GBP", "withdrawal", "Large purchase");
        when(bankAccountService.findAccountAndVerifyOwnership(accountNumber, userId)).thenReturn(bankAccount);

        InvalidOperationException exception
                = assertThrows(InvalidOperationException
                .class, () -> transactionService
                .createTransaction(accountNumber, withdrawalRequest, userId));

        assertEquals("Insufficient funds for withdrawal.", exception.getMessage());
        assertEquals(new BigDecimal("1000.00"), bankAccount.getBalance());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("createTransaction should throw InvalidOperationException for invalid type")
    void createTransaction_whenInvalidType_shouldThrowException() {
        // Arrange
        CreateTransactionRequestDto invalidRequest = new CreateTransactionRequestDto(new BigDecimal("50.00"), "GBP", "transfer", "Invalid");
        when(bankAccountService.findAccountAndVerifyOwnership(accountNumber, userId)).thenReturn(bankAccount);

        // Act & Assert
        InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
            transactionService.createTransaction(accountNumber, invalidRequest, userId);
        });

        assertEquals("Invalid transaction type: transfer", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }
}