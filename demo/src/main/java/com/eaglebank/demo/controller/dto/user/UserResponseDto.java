package com.eaglebank.demo.controller.dto.user;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public class UserResponseDto{
    String id;
    String name;
    AddressDto address;
    String email;
    String phoneNumber;
    OffsetDateTime createdTimestamp;
    OffsetDateTime updatedTimestamp;
}
