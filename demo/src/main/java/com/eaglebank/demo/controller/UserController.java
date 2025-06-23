package com.eaglebank.demo.controller;

import com.eaglebank.demo.controller.dto.user.CreateUserRequestDto;
import com.eaglebank.demo.controller.dto.user.UpdateUserRequestDto;
import com.eaglebank.demo.controller.dto.user.UserResponseDto;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto request) {
        UserResponseDto response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> fetchUserByID(@PathVariable String userId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getUserById(userId, jwt.getSubject()));
    }
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String userId
            , @AuthenticationPrincipal Jwt jwt, @RequestBody UpdateUserRequestDto request) {
        return ResponseEntity.ok(userService.updateUser(userId, request, jwt.getSubject()));
    }
}
