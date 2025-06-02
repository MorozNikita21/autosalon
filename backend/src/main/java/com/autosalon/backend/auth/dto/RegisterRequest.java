package com.autosalon.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RegisterRequest {
    @NotBlank
    @Size(min = 4, max = 30)
    private String login;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(min = 6, max = 40)
    private String confirmPassword;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private String birthday;

    @NotBlank
    private String address;

    @NotBlank
    private String passport;

    @NotBlank
    private String driverLicense;

    @NotNull
    private String firstLicenseDate;
}
