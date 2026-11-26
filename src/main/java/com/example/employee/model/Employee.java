package com.example.employee.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

// LEGACY: Hibernate-specific constraint annotations (Bean Validation 1.1 has no @Email / @NotBlank of its own).
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class Employee {

    private Long id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "department is required")
    private String department;

    @NotNull(message = "salary is required")
    @DecimalMin(value = "0.0", message = "salary must not be negative")
    private BigDecimal salary;

    // LEGACY: BV 1.1 cannot validate java.time types, so "not in the future" is checked by hand in EmployeeService.
    @NotNull(message = "hireDate is required")
    private LocalDate hireDate;

    public Employee() {
    }

    public Employee(Long id, String name, String email, String department, BigDecimal salary, LocalDate hireDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
