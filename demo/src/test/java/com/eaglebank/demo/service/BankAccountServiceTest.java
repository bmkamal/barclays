// Place this file in: src/test/java/com/eaglebank/demo/service/BankAccountServiceTest.java
package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.bankaccount.BankAccountResponseDto;
import com.eaglebank.demo.controller.dto.bankaccount.CreateBankAccountRequestDto;
import com.eaglebank.demo.exception.NotFoundException;
import com.eaglebank.demo.model.BankAccount;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.repository.BankAccountRepository;
import com.eaglebank.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private User user;
    private BankAccount bankAccount;
    private String userId;
    private String accountNumber;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        accountNumber = "12345678";

        user = User.builder().id(userId).name("Test User").build();

        bankAccount = BankAccount.builder()
                .accountNumber(accountNumber)
                .name("Test Account")
                .accountType("personal")
                .balance(BigDecimal.ZERO)
                .user(user)
                .createdTimestamp(OffsetDateTime.now())
                .updatedTimestamp(OffsetDateTime.now())
                .build();
    }

    // =================================================================================
    // Tests for createBankAccount()
    // =================================================================================

    @Test
    @DisplayName("createBankAccount should successfully create an account when user exists")
    void createBankAccount_whenUserExists_shouldSaveAndReturnDto() {
        // Arrange
        CreateBankAccountRequestDto request = new CreateBankAccountRequestDto("Savings Account", "personal");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Since toResponseDto() is on the entity, we mock the entity that save() returns
        BankAccount savedAccountMock = mock(BankAccount.class);
        when(accountRepository.save(any(BankAccount.class))).thenReturn(savedAccountMock);

        BankAccountResponseDto expectedResponse = BankAccountResponseDto.builder()
                .accountNumber(accountNumber)
                .name("Savings Account")
                .accountType("personal")
                .balance(BigDecimal.ZERO)
                .currency("GBP")
                .sortCode("10-10-10")
                .createdTimestamp(null)
                .updatedTimestamp(null)
                .build();
        when(savedAccountMock.toResponseDto()).thenReturn(expectedResponse);


        // Act
        BankAccountResponseDto result = bankAccountService.createBankAccount(request, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Savings Account", result.getName());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(userRepository, times(1)).findById(userId);
        verify(accountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("createBankAccount should throw NotFoundException when user does not exist")
    void createBankAccount_whenUserDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        CreateBankAccountRequestDto request = new CreateBankAccountRequestDto("Savings Account", "personal");
        String nonExistentUserId = "non-existent-id";
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bankAccountService.createBankAccount(request, nonExistentUserId);
        });

        assertEquals("User not found with ID: " + nonExistentUserId, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    // =================================================================================
    // Tests for findAccountsByUserId()
    // =================================================================================

    @Test
    @DisplayName("findAccountsByUserId should return a list of accounts for a valid user")
    void findAccountsByUserId_whenAccountsExist_shouldReturnDtoList() {
        // Arrange
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(bankAccount));

        // Act
        List<BankAccountResponseDto> results = bankAccountService.findAccountsByUserId(userId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(bankAccount.getName(), results.get(0).getName());
        verify(accountRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("findAccountsByUserId should return an empty list when user has no accounts")
    void findAccountsByUserId_whenNoAccountsExist_shouldReturnEmptyList() {
        // Arrange
        when(accountRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<BankAccountResponseDto> results = bankAccountService.findAccountsByUserId(userId);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(accountRepository, times(1)).findByUserId(userId);
    }

    // =================================================================================
    // Tests for findAccountAndVerifyOwnership()
    // =================================================================================

    @Test
    @DisplayName("findAccountAndVerifyOwnership should return account when ownership is correct")
    void findAccountAndVerifyOwnership_whenOwnershipCorrect_shouldReturnAccount() {
        // Arrange
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));

        // Act
        BankAccount result = bankAccountService.findAccountAndVerifyOwnership(accountNumber, userId);

        // Assert
        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("findAccountAndVerifyOwnership should throw NotFoundException when account does not exist")
    void findAccountAndVerifyOwnership_whenAccountNotFound_shouldThrowNotFoundException() {
        // Arrange
        String fakeAccountNumber = "99999999";
        when(accountRepository.findByAccountNumber(fakeAccountNumber)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bankAccountService.findAccountAndVerifyOwnership(fakeAccountNumber, userId);
        });

        assertEquals("Bank account not found with account number: " + fakeAccountNumber, exception.getMessage());
    }

    @Test
    @DisplayName("findAccountAndVerifyOwnership should throw NotFoundException when ownership is incorrect")
    void findAccountAndVerifyOwnership_whenOwnershipIncorrect_shouldThrowNotFoundException() {
        // Arrange
        String otherUserId = "other-user-id";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bankAccountService.findAccountAndVerifyOwnership(accountNumber, otherUserId);
        });

        assertEquals("This asset does not belong to your account.", exception.getMessage());
    }
}