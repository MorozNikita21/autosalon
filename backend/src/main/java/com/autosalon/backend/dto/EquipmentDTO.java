package com.autosalon.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDTO {

    private Long equipmentId;

    @NotNull
    private Long modelId;

    @Size(max = 100)
    private String name;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Size(max = 100)
    private String engine;

    @Size(max = 100)
    private String drives;

    @Size(max = 50)
    private String color;

    @Size(max = 50)
    private String salon;
}
