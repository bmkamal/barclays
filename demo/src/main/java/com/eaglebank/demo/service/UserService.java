package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.user.CreateUserRequestDto;
import com.eaglebank.demo.controller.dto.user.UpdateUserRequestDto;
import com.eaglebank.demo.controller.dto.user.UserResponseDto;
import com.eaglebank.demo.exception.ForbiddenException;
import com.eaglebank.demo.exception.GlobalExceptionHandler;
import com.eaglebank.demo.model.Address;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto request) {
        // Check if user with the same email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            //TODO: Use custom exception instead of GlobalExceptionHandler
            throw new ForbiddenException("User with email " + request.getEmail() + " already exists.");
        }
        User user = User.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(Address.fromDto(request.getAddress()))
                .createdTimestamp(OffsetDateTime.now())
                .updatedTimestamp(OffsetDateTime.now())
                .build();
        return userRepository.save(user).toResponseDto();
    }

    public UserResponseDto getUserById(String userId, String principalId) {
        if (!userId.equals(principalId)) {
            throw new ForbiddenException("This URI does not belong to your account.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found with ID: " + userId));
        return user.toResponseDto();

    }

    @Transactional
    public UserResponseDto updateUser(String userId, UpdateUserRequestDto request, String principalId) {
        if (!userId.equals(principalId)) {
            throw new ForbiddenException("This URI does not belong to your account.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("User not found with ID: " + userId));


        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(Address.fromDto(request.getAddress()));
        user.setUpdatedTimestamp(OffsetDateTime.now());
        return userRepository.save(user).toResponseDto();
    }

}
