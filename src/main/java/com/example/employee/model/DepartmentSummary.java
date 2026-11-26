package com.example.employee.model;

import java.math.BigDecimal;

// LEGACY: a plain class with constructor + getters (+ equals/hashCode/toString if you want them).
public class DepartmentSummary {

    private final String department;
    private final long count;
    private final BigDecimal averageSalary;

    public DepartmentSummary(String department, long count, BigDecimal averageSalary) {
        this.department = department;
        this.count = count;
        this.averageSalary = averageSalary;
    }

    public String getDepartment() {
        return department;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }
}
