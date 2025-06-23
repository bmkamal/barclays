package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateUserRequestDto{
        private String name;
        @Valid
        private AddressDto address;
        @Email
        private String email;
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
        private String phoneNumber;
}
