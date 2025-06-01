package com.autosalon.backend.dto;

import jakarta.validation.constraints.NotBlank;
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
public class AccountDTO {

    @NotBlank
    @Size(max = 30)
    private String login;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    @Size(max = 30)
    private String phoneNumber;
}
