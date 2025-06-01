package com.autosalon.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_drive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_drive_id")
    private Long testDriveId;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "drive_date")
    private LocalDateTime driveDate;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "hours")
    private Integer hours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", insertable = false, updatable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;
}
