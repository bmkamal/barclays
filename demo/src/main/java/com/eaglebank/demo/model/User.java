package com.eaglebank.demo.model;

import com.eaglebank.demo.controller.dto.user.UserResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Embedded
    private Address address;
    private String email;
    private String password;
    private String phoneNumber;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<BankAccount> accounts;
    private OffsetDateTime createdTimestamp;
    private OffsetDateTime updatedTimestamp;

    public UserResponseDto toResponseDto() {
        return UserResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .address(this.address.toDto())
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .createdTimestamp(this.createdTimestamp)
                .updatedTimestamp(this.updatedTimestamp)
                .build();
    }
}
