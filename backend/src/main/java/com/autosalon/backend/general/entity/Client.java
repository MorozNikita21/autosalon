package com.autosalon.backend.general.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "login", nullable = false, length = 30)
    private String login;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "passport", length = 50)
    private String passport;

    @Column(name = "driver_license", length = 50)
    private String driverLicense;

    @Column(name = "first_license_date")
    private LocalDate firstLicenseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login", referencedColumnName = "login", insertable = false, updatable = false)
    private Account account;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orders> orders;
}
