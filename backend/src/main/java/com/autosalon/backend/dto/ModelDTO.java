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
public class ModelDTO {

    private Long modelId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String mark;
}
