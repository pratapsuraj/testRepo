package com.example.employee.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.example.employee.model.Employee;

public class EmployeeUtilsTest {

    private static Employee emp(String name) {
        return new Employee(1L, name, "x@example.com", "HR", BigDecimal.TEN, LocalDate.of(2020, 1, 1));
    }

    @Test
    public void formatsDate() {
        assertEquals("2021-03-09", EmployeeUtils.formatHireDate(LocalDate.of(2021, 3, 9)));
    }

    @Test
    public void joinsAndSortsNames() {
        List<Employee> list = Arrays.asList(emp("bob"), emp("Alice"));
        assertEquals("bob, Alice", EmployeeUtils.joinNames(list));
        assertEquals("Alice, bob", EmployeeUtils.joinNames(EmployeeUtils.sortByName(list)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void defaultDepartmentsIsImmutable() {
        EmployeeUtils.defaultDepartments().add("X");
    }

    @Test
    public void defaultDepartmentsContent() {
        assertEquals(Arrays.asList("Engineering", "HR", "Finance"), EmployeeUtils.defaultDepartments());
    }

    @Test
    public void emailDomain() {
        assertEquals("example.com", EmployeeUtils.emailDomain("a@example.com").get());
        assertFalse(EmployeeUtils.emailDomain("nope").isPresent());
        assertFalse(EmployeeUtils.emailDomain(null).isPresent());
    }

    @Test
    public void describeAndLabels() {
        assertEquals("Employee:bob", EmployeeUtils.describe(emp("bob")));
        assertEquals("Number:7", EmployeeUtils.describe(7.9));
        assertEquals("String:hi", EmployeeUtils.describe("  hi "));
        assertEquals("Unknown", EmployeeUtils.describe(new Object()));
        assertEquals("Senior", EmployeeUtils.levelLabel(3));
        assertEquals("Unknown", EmployeeUtils.levelLabel(9));
        assertEquals("Employee Service\n  version: 1.0\n  status: ready\n", EmployeeUtils.banner());
        assertTrue(EmployeeUtils.isHighEarner(new BigDecimal("100000")));
    }

    @Test
    public void reflectionReadsPrivateJdkField() throws Exception {
        List<String> list = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        assertEquals(3, ReflectionDemo.internalSizeOf(list));
    }

    @Test
    public void jaxbMarshalsEmployee() {
        String xml = JaxbUtil.toXml(emp("bob"));
        assertTrue(xml, xml.contains("<name>bob</name>"));
        assertTrue(xml, xml.contains("<employee>"));
    }
}
