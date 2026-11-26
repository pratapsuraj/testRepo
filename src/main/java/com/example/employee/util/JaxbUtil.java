package com.example.employee.util;

import java.io.StringWriter;
import java.math.BigDecimal;

// LEGACY: javax.xml.bind is part of the JDK 8 (java.xml.bind module, removed in JDK 11) - no dependency needed.
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.employee.model.Employee;

public final class JaxbUtil {

    private JaxbUtil() {
    }

    @XmlRootElement(name = "employee")
    public static class EmployeeXml {
        public Long id;
        public String name;
        public String email;
        public String department;
        public BigDecimal salary;

        public EmployeeXml() {
        }

        public EmployeeXml(Employee e) {
            this.id = e.getId();
            this.name = e.getName();
            this.email = e.getEmail();
            this.department = e.getDepartment();
            this.salary = e.getSalary();
        }
    }

    public static String toXml(Employee employee) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(EmployeeXml.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            StringWriter out = new StringWriter();
            m.marshal(new EmployeeXml(employee), out);
            return out.toString();
        } catch (JAXBException ex) {
            throw new IllegalStateException("XML marshalling failed", ex);
        }
    }
}
