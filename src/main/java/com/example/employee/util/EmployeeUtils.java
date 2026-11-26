package com.example.employee.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.employee.model.Employee;

/** Small helper methods whose implementation differs between Java 8 and Java 17 style. */
public final class EmployeeUtils {

    private EmployeeUtils() {
    }

    // LEGACY: java.util.Date + SimpleDateFormat (not thread-safe, so a new instance each call).
    public static String formatHireDate(LocalDate date) {
        Date legacy = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new SimpleDateFormat("yyyy-MM-dd").format(legacy);
    }

    // LEGACY: loop + String.join (Java 8).
    public static String joinNames(List<Employee> employees) {
        List<String> names = new ArrayList<String>();
        for (Employee e : employees) {
            names.add(e.getName());
        }
        return String.join(", ", names);
    }

    // LEGACY: wrap in Collections.unmodifiableList - there is no List.of in Java 8.
    public static List<String> defaultDepartments() {
        return Collections.unmodifiableList(new ArrayList<String>(Arrays.asList("Engineering", "HR", "Finance")));
    }

    // LEGACY: anonymous inner class Comparator.
    public static List<Employee> sortByName(List<Employee> employees) {
        List<Employee> copy = new ArrayList<Employee>(employees);
        Collections.sort(copy, new Comparator<Employee>() {
            @Override
            public int compare(Employee a, Employee b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        return copy;
    }

    // LEGACY: Optional + ternary; no @Nullable annotation on the parameter.
    public static Optional<String> emailDomain(String email) {
        if (email == null || email.indexOf('@') < 0) {
            return Optional.empty();
        }
        return Optional.of(email.substring(email.indexOf('@') + 1));
    }

    // LEGACY: instanceof followed by an explicit cast.
    public static String describe(Object o) {
        if (o instanceof Employee) {
            Employee e = (Employee) o;
            return "Employee:" + e.getName();
        } else if (o instanceof Number) {
            Number n = (Number) o;
            return "Number:" + n.intValue();
        } else if (o instanceof String) {
            String s = (String) o;
            return "String:" + s.trim();
        }
        return "Unknown";
    }

    // LEGACY: classic switch statement with break/return.
    public static String levelLabel(int level) {
        switch (level) {
            case 1:
                return "Junior";
            case 2:
                return "Mid";
            case 3:
                return "Senior";
            default:
                return "Unknown";
        }
    }

    // LEGACY: multi-line string built with concatenation and explicit \n.
    public static String banner() {
        return "Employee Service\n"
                + "  version: 1.0\n"
                + "  status: ready\n";
    }

    public static boolean isHighEarner(BigDecimal salary) {
        return salary.compareTo(new BigDecimal("100000")) >= 0;
    }
}
