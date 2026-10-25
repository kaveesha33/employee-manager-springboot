package com.example.employee_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required.")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(
        regexp = "^07\\d{8}$",
        message = "Enter a valid Sri Lankan mobile number (07XXXXXXXX)."
    )
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Department is required.")
    @Column(nullable = false)
    private String department;

    @NotNull(message = "Salary is required.")
    @Positive(message = "Salary must be greater than zero.")
    @Column(nullable = false)
    private Double salary;
}