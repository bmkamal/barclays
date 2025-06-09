package com.eaglebank.demo.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddressDto{
    @NotBlank
    String line1;
    private String line2;
    private String line3;
    @NotBlank
    private String town;
    @NotBlank
    private String county;
    @NotBlank
    private String postcode;
}

