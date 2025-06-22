package com.eaglebank.demo.service;


import com.eaglebank.demo.controller.dto.user.LoginRequestDto;
import com.eaglebank.demo.controller.dto.user.TokenResponseDto;
import com.eaglebank.demo.exception.NotFoundException;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    @Value("${jwt.issuer}")
    private String issuer;

    public AuthService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    public TokenResponseDto issueToken(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        String userId = userRepository.findByEmail(loginRequest.getEmail())
                .map(User::getId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Instant now = Instant.now();
        long expiry = 36000L;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(userId)
                .build();

        String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenResponseDto(token);
    }
}

