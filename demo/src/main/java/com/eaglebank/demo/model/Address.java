package com.eaglebank.demo.model;

import com.eaglebank.demo.controller.dto.user.AddressDto;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {
    private String line1;
    private String line2;
    private String line3;
    private String town;
    private String county;
    private String postcode;

    public static Address fromDto(AddressDto addressDto) {
        return Address.builder()
                .line1(addressDto.getLine1())
                .line2(addressDto.getLine2())
                .line3(addressDto.getLine3())
                .town(addressDto.getTown())
                .county(addressDto.getCounty())
                .postcode(addressDto.getPostcode())
                .build();
    }
    public AddressDto toDto() {
        return AddressDto.builder()
                .line1(this.line1)
                .line2(this.line2)
                .line3(this.line3)
                .town(this.town)
                .county(this.county)
                .postcode(this.postcode)
                .build();
    }
}
