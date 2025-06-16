package com.eaglebank.demo.repository;

import com.eaglebank.demo.model.BankAccount;
import com.eaglebank.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByBankAccount(BankAccount bankAccount);

    Optional<Transaction> findByIdAndBankAccount_AccountNumber(String transactionId, String accountNumber);

    List<Transaction> findByBankAccount_AccountNumber(String accountNumber);
}
