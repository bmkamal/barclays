package com.eaglebank.demo.controller.dto.user;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class UserResponseDto{
    private final String id;
    private final String name;
    private final AddressDto address;
    private final String email;
    private final String phoneNumber;
    private final OffsetDateTime createdTimestamp;
    private final OffsetDateTime updatedTimestamp;
}
