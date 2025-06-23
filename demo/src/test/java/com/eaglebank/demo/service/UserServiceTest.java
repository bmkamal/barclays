package com.eaglebank.demo.service;

import com.eaglebank.demo.controller.dto.user.CreateUserRequestDto;
import com.eaglebank.demo.controller.dto.user.UpdateUserRequestDto;
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
import org.mockito.ArgumentCaptor;
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
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        Address address = Address
                .builder().line1("123 Test St")
                .town("Testville")
                .build();
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
    void givenUniqueEmailCreateUser() {
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


        UserResponseDto result = userService.createUser(createUserRequestDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    @DisplayName("createUser should throw ForbiddenException when email already exists")
    void givenAlreadyRegisteredEmailToCreateUserThrowsForbiddenException() {
        when(userRepository.findByEmail(createUserRequestDto.getEmail())).thenReturn(Optional.of(user));

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.createUser(createUserRequestDto);
        });

        assertEquals("User with email " + createUserRequestDto.getEmail() + " already exists.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    @DisplayName("getUserById should return user when userId matches principalId")
    void givenValidPrincipalAndUserIdsReturnsUser() {
        String userId = user.getId();
        String principalId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(userId, principalId);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getUserById should throw ForbiddenException when userId does not match principalId")
    void givenDifferentPrincipalAndUserIdWhenGetUserByIdThrowsForbiddenException() {
        String userId = "some-other-user-id";
        String principalId = user.getId();

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.getUserById(userId, principalId);
        });

        assertEquals("This URI does not belong to your account.", exception.getMessage());
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("getUserById should throw ForbiddenException when user is not found")
    void givenInvalidUserIdWhenGetUserByIdThrowsForbiddenException() {
        String userId = user.getId();
        String principalId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            userService.getUserById(userId, principalId);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenValidUserIdAndRequestWhenUpdateUserThenReturnUpdatedUser() {
        //AddressDto updatedAddressDto =  addressDto;
        String principalId = userId;
        UpdateUserRequestDto updateRequest = UpdateUserRequestDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .phoneNumber("+2222222222")
                        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto result = userService.updateUser(userId, updateRequest, principalId);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("+2222222222", result.getPhoneNumber());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        assertEquals("Updated Name", userCaptor.getValue().getName());


    }
}