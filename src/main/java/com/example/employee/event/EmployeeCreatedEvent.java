package com.example.employee.event;

import org.springframework.context.ApplicationEvent;

import com.example.employee.model.Employee;

// LEGACY: events conventionally extend ApplicationEvent (POJO events are supported since 4.2 but rarely used).
public class EmployeeCreatedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Employee employee;

    public EmployeeCreatedEvent(Object source, Employee employee) {
        super(source);
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }
}
