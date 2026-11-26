package com.example.employee.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employee.event.EmployeeCreatedEvent;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.model.DepartmentSummary;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public EmployeeService(EmployeeRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll(String department) {
        return department == null ? repository.findAll() : repository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public Employee getById(long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee create(Employee employee) {
        requireNotFuture(employee);
        Employee saved = repository.insert(employee);
        // LEGACY: event type extends ApplicationEvent.
        publisher.publishEvent(new EmployeeCreatedEvent(this, saved));
        return saved;
    }

    public Employee update(long id, Employee employee) {
        requireNotFuture(employee);
        if (repository.update(id, employee) == 0) {
            throw new EmployeeNotFoundException(id);
        }
        employee.setId(id);
        return employee;
    }

    public void delete(long id) {
        if (repository.delete(id) == 0) {
            throw new EmployeeNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    public List<DepartmentSummary> summarizeByDepartment() {
        // LEGACY: manual grouping with a loop and a TreeMap (Java 8 streams would also work, this is the "old habit" style).
        Map<String, List<Employee>> byDept = new TreeMap<String, List<Employee>>();
        for (Employee e : repository.findAll()) {
            List<Employee> list = byDept.get(e.getDepartment());
            if (list == null) {
                list = new ArrayList<Employee>();
                byDept.put(e.getDepartment(), list);
            }
            list.add(e);
        }
        List<DepartmentSummary> result = new ArrayList<DepartmentSummary>();
        for (Map.Entry<String, List<Employee>> entry : byDept.entrySet()) {
            BigDecimal total = BigDecimal.ZERO;
            for (Employee e : entry.getValue()) {
                total = total.add(e.getSalary());
            }
            BigDecimal avg = total.divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);
            result.add(new DepartmentSummary(entry.getKey(), entry.getValue().size(), avg));
        }
        Collections.sort(result, new Comparator<DepartmentSummary>() {
            @Override
            public int compare(DepartmentSummary a, DepartmentSummary b) {
                return a.getDepartment().compareTo(b.getDepartment());
            }
        });
        return result;
    }

    // LEGACY: Bean Validation 1.1 has no @PastOrPresent / java.time support, so the rule lives here.
    private void requireNotFuture(Employee e) {
        if (e.getHireDate() != null && e.getHireDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("hireDate must not be in the future");
        }
    }
}
