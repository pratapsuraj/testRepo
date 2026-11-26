package com.example.employee.web;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dto.ApiResponse;
import com.example.employee.model.DepartmentSummary;
import com.example.employee.model.Employee;
import com.example.employee.service.EmployeeService;
import com.example.employee.util.JaxbUtil;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    // LEGACY: implicit constructor injection (single constructor) works since Spring 4.3.
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // LEGACY: @RequestMapping(method = ...) - Spring 4.3 also has @GetMapping, but older code uses the long form.
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<Employee>> list(@RequestParam(value = "department", required = false) String department) {
        return ApiResponse.ok(service.findAll(department));
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<DepartmentSummary>> summary() {
        return ApiResponse.ok(service.summarizeByDepartment());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Employee> get(@PathVariable("id") long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @RequestMapping(value = "/{id}/xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public String xml(@PathVariable("id") long id) {
        return JaxbUtil.toXml(service.getById(id));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Employee>> create(@Valid @RequestBody Employee employee) {
        Employee saved = service.create(employee);
        return ResponseEntity.created(URI.create("/api/employees/" + saved.getId()))
                .body(ApiResponse.ok("created", saved));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Employee> update(@PathVariable("id") long id, @Valid @RequestBody Employee employee) {
        return ApiResponse.ok("updated", service.update(id, employee));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        service.delete(id);
    }
}
