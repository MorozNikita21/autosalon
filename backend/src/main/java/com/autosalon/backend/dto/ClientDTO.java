package com.autosalon.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Long clientId;

    @NotBlank
    @Size(max = 30)
    private String login;

    @Size(max = 100)
    private String name;

    @Email
    @Size(max = 100)
    private String email;

    private LocalDate birthday;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String passport;

    @Size(max = 50)
    private String driverLicense;

    private LocalDate firstLicenseDate;
}
