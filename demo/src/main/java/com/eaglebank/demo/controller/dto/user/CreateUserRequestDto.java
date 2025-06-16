package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequestDto{
        @NotBlank
        private String name;
        @NotNull
        @Valid
        private AddressDto address;
        @NotBlank @Email
        private String email;
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
        private String phoneNumber;
        @NotBlank
        @Size(min = 8)
        private String password;
}