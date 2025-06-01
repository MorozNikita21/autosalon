package com.autosalon.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class OrderDTO {

    private Long orderId;

    @NotNull
    private Long clientId;

    @NotNull
    private Long carId;

    @NotNull
    private Long employeeId;

    private LocalDate orderDate;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal clearPrice;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
}
