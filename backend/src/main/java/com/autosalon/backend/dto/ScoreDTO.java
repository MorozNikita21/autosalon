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
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreDTO {

    private Long scoreId;

    @NotNull
    private Long orderId;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal sum;

    @Size(max = 20)
    private String status;

    private LocalDate scoreDate;
}
