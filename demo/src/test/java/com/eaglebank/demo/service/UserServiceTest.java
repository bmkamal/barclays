package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.user.CreateUserRequestDto;
import com.eaglebank.demo.controller.dto.user.UserResponseDto;
import com.eaglebank.demo.controller.dto.user.AddressDto;
import com.eaglebank.demo.exception.ForbiddenException;
import com.eaglebank.demo.model.Address;
import com.eaglebank.demo.model.User;
import com.eaglebank.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserRequestDto createUserRequestDto;

    @BeforeEach
    void setUp() {
        // Shared test data setup
        String userId = UUID.randomUUID().toString();
        Address address = Address.builder().line1("123 Test St").town("Testville").build();
        user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword")
                .phoneNumber("+4412345678900")
                .address(address)
                .createdTimestamp(OffsetDateTime.now())
                .updatedTimestamp(OffsetDateTime.now())
                .build();

        AddressDto addressDto = AddressDto.builder()
                .line1("123 Test St")
                .town("Testville")
                .county("Test County")
                .postcode("T1 2ST")
                .build();

        createUserRequestDto = CreateUserRequestDto.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("+1234567890")
                .address(addressDto)
                .build();

    }


    @Test
    @DisplayName("createUser should successfully create and save a new user")
    void createUser_whenEmailIsUnique_shouldSaveAndReturnUser() {
        when(userRepository.findByEmail(createUserRequestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createUserRequestDto.getPassword())).thenReturn("hashedPassword");

        User savedUserMock = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(savedUserMock);
        when(savedUserMock.toResponseDto()).thenReturn(
                UserResponseDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .address(null)
                        .createdTimestamp(null)
                        .updatedTimestamp(null)
                        .build()
        );


        // Act: Call the method under test
        UserResponseDto result = userService.createUser(createUserRequestDto);

        // Assert: Verify the outcome
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());

        // Verify that the save method was called exactly once on the repository
        verify(userRepository, times(1)).save(any(User.class));
        // Verify that the password encoder was called exactly once
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("createUser should throw ForbiddenException when email already exists")
    void createUser_whenEmailExists_shouldThrowForbiddenException() {
        // Arrange: Mock the repository to indicate the email already exists
        when(userRepository.findByEmail(createUserRequestDto.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert: Check that the correct exception is thrown
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.createUser(createUserRequestDto);
        });

        assertEquals("User with email " + createUserRequestDto.getEmail() + " already exists.", exception.getMessage());

        // Verify that the save method was NEVER called
        verify(userRepository, never()).save(any(User.class));
    }

    // =================================================================================
    // Tests for getUserById()
    // =================================================================================

    @Test
    @DisplayName("getUserById should return user when userId matches principalId")
    void getUserById_whenIdsMatch_shouldReturnUser() {
        // Arrange
        String userId = user.getId();
        String principalId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponseDto result = userService.getUserById(userId, principalId);

        // Assert
        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getUserById should throw ForbiddenException when userId does not match principalId")
    void getUserById_whenIdsDoNotMatch_shouldThrowForbiddenException() {
        // Arrange
        String userId = "some-other-user-id";
        String principalId = user.getId();

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.getUserById(userId, principalId);
        });

        assertEquals("This URI does not belong to your account.", exception.getMessage());

        // Verify that the repository was never even called, as the check fails first
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("getUserById should throw ForbiddenException when user is not found")
    void getUserById_whenUserNotFound_shouldThrowForbiddenException() {
        // Arrange
        String userId = user.getId();
        String principalId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.getUserById(userId, principalId);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}