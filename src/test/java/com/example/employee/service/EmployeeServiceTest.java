package com.example.employee.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
// LEGACY: Mockito 2.x runner (org.mockito.junit.MockitoJUnitRunner); Mockito 1.x used org.mockito.runners.MockitoJUnitRunner.
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import com.example.employee.event.EmployeeCreatedEvent;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.model.DepartmentSummary;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private EmployeeService service;

    private Employee sample(Long id, String dept, String salary) {
        return new Employee(id, "Test " + id, "t" + id + "@example.com", dept, new BigDecimal(salary), LocalDate.of(2020, 1, 1));
    }

    @Test
    public void createSavesAndPublishesEvent() {
        Employee input = sample(null, "Engineering", "1000");
        when(repository.insert(input)).thenReturn(sample(5L, "Engineering", "1000"));

        Employee saved = service.create(input);

        assertEquals(Long.valueOf(5L), saved.getId());
        ArgumentCaptor<EmployeeCreatedEvent> captor = ArgumentCaptor.forClass(EmployeeCreatedEvent.class);
        // LEGACY: publishEvent(ApplicationEvent) overload is picked for ApplicationEvent subclasses.
        verify(publisher).publishEvent(captor.capture());
        assertSame(saved, captor.getValue().getEmployee());
    }

    @Test(expected = EmployeeNotFoundException.class)
    public void getByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.<Employee>empty());
        service.getById(99L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRejectsFutureHireDate() {
        Employee e = sample(null, "HR", "10");
        e.setHireDate(LocalDate.now().plusDays(1));
        service.create(e);
    }

    @Test
    public void summarizeGroupsByDepartment() {
        List<Employee> all = Arrays.asList(sample(1L, "HR", "100"), sample(2L, "HR", "200"), sample(3L, "Engineering", "300"));
        when(repository.findAll()).thenReturn(all);

        List<DepartmentSummary> result = service.summarizeByDepartment();

        assertEquals(2, result.size());
        assertEquals("Engineering", result.get(0).getDepartment());
        assertEquals(2L, result.get(1).getCount());
        assertEquals(new BigDecimal("150.00"), result.get(1).getAverageSalary());
    }
}
