package com.eaglebank.demo.controller;

import com.eaglebank.demo.controller.dto.user.LoginRequestDto;
import com.eaglebank.demo.controller.dto.user.TokenResponseDto;
import com.eaglebank.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> issueToken(@Valid @RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.issueToken(loginRequest));
    }
}
