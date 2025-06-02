package com.autosalon.backend.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
