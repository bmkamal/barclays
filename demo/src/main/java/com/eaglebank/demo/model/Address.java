package com.eaglebank.demo.model;

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
}
