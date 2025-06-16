package com.eaglebank.demo.repository;

import com.eaglebank.demo.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    List<BankAccount> findByUserId(String userId);

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
