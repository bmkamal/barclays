package com.eaglebank.demo.controller;

import com.eaglebank.demo.controller.dto.bankaccount.BankAccountResponseDto;
import com.eaglebank.demo.controller.dto.bankaccount.CreateBankAccountRequestDto;
import com.eaglebank.demo.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/v1/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponseDto> createBankAccount(@Valid @RequestBody CreateBankAccountRequestDto request, @AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(bankAccountService
                .createBankAccount(request, jwt.getSubject()), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<BankAccountResponseDto>> listAccounts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bankAccountService.findAccountsByUserId(jwt.getSubject()));
    }
    
}
