package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddressDto{
    @NotBlank
    private final String line1;
    private final String line2;
    private final String line3;
    @NotBlank
    private final String town;
    @NotBlank
    private final String county;
    @NotBlank
    private final String postcode;
}

