package com.autosalon.backend.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUpdateDto {

    @NotBlank
    private String login;

    @NotBlank
    private String name;

    @Email
    private String email;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    private String address;

    private String passport;

    private String driverLicense;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate firstLicenseDate;
}
