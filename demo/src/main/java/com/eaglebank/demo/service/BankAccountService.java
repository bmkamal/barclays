package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.bankaccount.BankAccountResponseDto;
import com.eaglebank.demo.controller.dto.bankaccount.CreateBankAccountRequestDto;
import com.eaglebank.demo.exception.NotFoundException;
import com.eaglebank.demo.model.BankAccount;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.repository.BankAccountRepository;
import com.eaglebank.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;

    public BankAccountService(BankAccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BankAccountResponseDto createBankAccount(CreateBankAccountRequestDto request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        return accountRepository.save(
                        BankAccount.builder()
                                .accountNumber(generateUniqueAccountNumber().toString())
                                .name(request.name()).accountType(request.accountType())
                                .balance(BigDecimal.ZERO).currency("GBP").sortCode("10-10-10")
                                .user(user).createdTimestamp(OffsetDateTime.now()).updatedTimestamp(OffsetDateTime.now())
                                .build()
                ).toResponseDto();
    }

    public List<BankAccountResponseDto> findAccountsByUserId(String userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(BankAccount::toResponseDto).collect(Collectors.toList());

    }

    private Integer generateUniqueAccountNumber() {
    //TODO: Implement repository with pre-generted Account numbers
        return ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
    }

    public BankAccount findAccountAndVerifyOwnership(String accountNumber, String principalId) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Bank account not found with account number: " + accountNumber));
        if (!account.getUser().getId().equals(principalId)) {
            throw new NotFoundException("This asset does not belong to your account.");
        }
        return account;
    }
}
