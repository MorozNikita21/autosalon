package com.autosalon.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class CarDTO {

    private Long carId;

    @NotNull
    private Long equipmentId;

    @NotBlank
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$",
            message = "VIN должен состоять из 17 допустимых символов")
    private String vin;
}
