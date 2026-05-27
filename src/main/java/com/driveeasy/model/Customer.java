package com.driveeasy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid Indian phone number")
    @Column(name = "phone", nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "driving_license_number", length = 20)
    private String drivingLicenseNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    protected Customer() { /* JPA only */ }

    /**
     * FIX: Removed the Phase-1 4-arg constructor (long id, name, email, phone).
     * CustomerDaoImpl called it; CustomerDaoImpl is deleted (dead code).
     * CustomerService and ConsoleApp both use this 4-arg (name,email,phone,license) form.
     */
    public Customer(String name, String email, String phone, String drivingLicenseNumber) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public Long getId()                       { return id; }
    public String getName()                   { return name; }
    public String getEmail()                  { return email; }
    public String getPhone()                  { return phone; }
    public String getDrivingLicenseNumber()   { return drivingLicenseNumber; }
    public List<Reservation> getReservations(){ return reservations; }

    public void setName(String name)                                    { this.name = name; }
    public void setEmail(String email)                                  { this.email = email; }
    public void setPhone(String phone)                                  { this.phone = phone; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber)    { this.drivingLicenseNumber = drivingLicenseNumber; }
}