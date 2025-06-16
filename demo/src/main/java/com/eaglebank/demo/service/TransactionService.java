package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.transaction.CreateTransactionRequestDto;
import com.eaglebank.demo.controller.dto.transaction.TransactionResponseDto;
import com.eaglebank.demo.exception.InvalidOperationException;
import com.eaglebank.demo.exception.NotFoundException;
import com.eaglebank.demo.model.BankAccount;
import com.eaglebank.demo.model.Transaction;
import com.eaglebank.demo.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;

    public TransactionService(TransactionRepository transactionRepository, BankAccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.bankAccountService = accountService;
    }

    @Transactional
    public TransactionResponseDto createTransaction(String accountNumber, CreateTransactionRequestDto request, String principalId) {
        BankAccount account = bankAccountService.findAccountAndVerifyOwnership(accountNumber, principalId);
        BigDecimal amount = request.amount();

        if ("withdrawal".equalsIgnoreCase(request.type())) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InvalidOperationException("Insufficient funds for withdrawal.");
            }
            account.setBalance(account.getBalance().subtract(amount));
        } else if ("deposit".equalsIgnoreCase(request.type())) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            throw new InvalidOperationException("Invalid transaction type: " + request.type());
        }
        account.setUpdatedTimestamp(OffsetDateTime.now());

        Transaction transaction = Transaction.builder()
                .bankAccount(account).amount(amount).type(request.type())
                .currency(request.currency()).reference(request.reference())
                .createdTimestamp(OffsetDateTime.now()).build();
        Transaction savedTransaction = transactionRepository.save(transaction);
        return savedTransaction.toResponseDto();
    }

    public List<TransactionResponseDto> findTransactionsByAccountNumber(String accountNumber, String principalId) {
        bankAccountService.findAccountAndVerifyOwnership(accountNumber, principalId);
        return transactionRepository.findByBankAccount_AccountNumber(accountNumber).stream()
                .map(Transaction::toResponseDto).collect(Collectors.toList());
    }

    public TransactionResponseDto findTransactionById(String transactionId, String accountNumber, String principalId) {
        bankAccountService.findAccountAndVerifyOwnership(accountNumber, principalId);
        Transaction transaction = transactionRepository.findByIdAndBankAccount_AccountNumber(transactionId, accountNumber)
                .orElseThrow(() -> new NotFoundException("Transaction with ID " + transactionId + " not found for this account."));
        return transaction.toResponseDto();
    }
    
}
