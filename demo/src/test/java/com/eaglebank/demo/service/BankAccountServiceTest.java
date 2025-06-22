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

    @Test
    @DisplayName("createBankAccount when user exists")
    void givenValidUserCreateBankAccount() {
        CreateBankAccountRequestDto request = new CreateBankAccountRequestDto("Savings Account", "personal");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

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

        BankAccountResponseDto result = bankAccountService.createBankAccount(request, userId);
        assertEquals("Savings Account", result.getName());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }


    @Test
    @DisplayName("createBankAccount should throw NotFoundException when user does not exist")
    void givenInvalidUserThrowNotFoundException() {
        CreateBankAccountRequestDto request = new CreateBankAccountRequestDto("Savings Account", "personal");
        String nonExistentUserId = "non-existent-id";
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bankAccountService.createBankAccount(request, nonExistentUserId);
        });

        assertEquals("User not found with ID: " + nonExistentUserId, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("findAccountsByUserId should return a list of accounts for a valid user")
    void givenUserIdAndBankAccountsExistReturnBankAccountList() {
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(bankAccount));

        List<BankAccountResponseDto> results = bankAccountService.findAccountsByUserId(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(bankAccount.getName(), results.get(0).getName());
    }

    @Test
    @DisplayName("findAccountsByUserId should return an empty list when user has no accounts")
    void givenUserIdAndBankAccountsDontExistReturnEmptyBankAccountList() {
        when(accountRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<BankAccountResponseDto> results = bankAccountService.findAccountsByUserId(userId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(accountRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("findAccountAndVerifyOwnership should return account when ownership is correct")
    void givenValidBankAccountNumberAndPrincipalIdReturnBankAccountModel() {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));

        BankAccount result = bankAccountService.findAccountAndVerifyOwnership(accountNumber, userId);

        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
    }

    @Test
    @DisplayName("findAccountAndVerifyOwnership should throw NotFoundException when account does not exist")
    void givenInValidBakAccountNumberThrowNotFoundException() {
        String invalidBankAccountNumber = "99999999";
        when(accountRepository.findByAccountNumber(invalidBankAccountNumber)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bankAccountService.findAccountAndVerifyOwnership(invalidBankAccountNumber, userId);
        });

        assertEquals("Bank account not found with account number: " + invalidBankAccountNumber, exception.getMessage());
    }

    @Test
    @DisplayName("findAccountAndVerifyOwnership should throw NotFoundException when ownership is incorrect")
    void givenInvalidPrincipalIdThrowNotFoundException() {
        String inValidPrincipalId = "invalid-principal-id";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bankAccountService.findAccountAndVerifyOwnership(accountNumber, inValidPrincipalId);
        });

        assertEquals("This asset does not belong to your account.", exception.getMessage());
    }
}