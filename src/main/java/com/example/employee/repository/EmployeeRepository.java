package com.example.employee.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.example.employee.model.Employee;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    // LEGACY: single constructor => implicit @Autowired since 4.3; annotation kept here only as documentation.
    @Autowired
    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // LEGACY: anonymous inner class RowMapper.
    private final RowMapper<Employee> rowMapper = new RowMapper<Employee>() {
        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Employee(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getBigDecimal("salary"),
                    rs.getDate("hire_date").toLocalDate());
        }
    };

    public List<Employee> findAll() {
        return jdbcTemplate.query("SELECT * FROM employee ORDER BY id", rowMapper);
    }

    public List<Employee> findByDepartment(String department) {
        // LEGACY: query(String, Object[], RowMapper) - deprecated in Spring 5.3 (use query(sql, rowMapper, args...)).
        return jdbcTemplate.query("SELECT * FROM employee WHERE department = ? ORDER BY id",
                new Object[] { department }, rowMapper);
    }

    public Optional<Employee> findById(long id) {
        List<Employee> result = jdbcTemplate.query("SELECT * FROM employee WHERE id = ?",
                new Object[] { id }, rowMapper);
        return result.isEmpty() ? Optional.<Employee>empty() : Optional.of(result.get(0));
    }

    public Employee insert(final Employee e) {
        // LEGACY: Assert message as plain String.
        Assert.notNull(e, "employee must not be null");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO employee(name, email, department, salary, hire_date) VALUES (?,?,?,?,?)",
                    new String[] { "id" });
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getDepartment());
            ps.setBigDecimal(4, e.getSalary());
            ps.setDate(5, Date.valueOf(e.getHireDate()));
            return ps;
        }, keyHolder);
        // LEGACY: getKey() has no @Nullable contract.
        e.setId(keyHolder.getKey().longValue());
        return e;
    }

    public int update(long id, Employee e) {
        return jdbcTemplate.update(
                "UPDATE employee SET name=?, email=?, department=?, salary=?, hire_date=? WHERE id=?",
                e.getName(), e.getEmail(), e.getDepartment(), e.getSalary(), Date.valueOf(e.getHireDate()), id);
    }

    public int delete(long id) {
        return jdbcTemplate.update("DELETE FROM employee WHERE id = ?", id);
    }

    public long count() {
        // LEGACY: queryForObject returns Long (could be null in theory; no @Nullable annotation).
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employee", Long.class);
    }
}
