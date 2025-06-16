package com.eaglebank.demo.controller;

import com.eaglebank.demo.controller.dto.transaction.CreateTransactionRequestDto;
import com.eaglebank.demo.controller.dto.transaction.TransactionResponseDto;
import com.eaglebank.demo.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService t) { this.transactionService = t; }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(@PathVariable String accountNumber, @Valid @RequestBody CreateTransactionRequestDto request, @AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(transactionService.createTransaction(accountNumber, request, jwt.getSubject()), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> listTransactions(@PathVariable String accountNumber, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(transactionService.findTransactionsByAccountNumber(accountNumber, jwt.getSubject()));
    }
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> fetchTransaction(@PathVariable String accountNumber, @PathVariable String transactionId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(transactionService.findTransactionById(transactionId, accountNumber, jwt.getSubject()));
    }
}
