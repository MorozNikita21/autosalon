package com.autosalon.backend.auth.dto;

import java.util.List;
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
public class JwtResponse {
    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String login;
    private List<String> roles;

    public JwtResponse(String accessToken, String login, List<String> roles) {
        this.token = accessToken;
        this.login = login;
        this.roles = roles;
    }
}
