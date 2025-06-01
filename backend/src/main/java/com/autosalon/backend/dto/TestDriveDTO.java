package com.autosalon.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDriveDTO {

    private Long testDriveId;

    @NotNull
    private Long carId;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long clientId;

    private LocalDateTime driveDate;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Size(max = 20)
    private String status;

    @Min(1)
    private Integer hours;
}
