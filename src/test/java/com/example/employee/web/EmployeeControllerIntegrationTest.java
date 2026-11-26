package com.example.employee.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.employee.config.AppConfig;
import com.example.employee.config.WebConfig;

// LEGACY: SpringJUnit4ClassRunner (SpringRunner alias exists since 4.3 too).
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { AppConfig.class, WebConfig.class })
public class EmployeeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private static final String VALID = "{\"name\":\"Carol\",\"email\":\"carol@example.com\","
            + "\"department\":\"Finance\",\"salary\":5000.50,\"hireDate\":\"2022-02-02\"}";

    @Test
    public void listReturnsSeedData() throws Exception {
        mvc.perform(get("/api/employees").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Alice Smith"))
                .andExpect(jsonPath("$.data[0].hireDate").value("2020-01-15"));
    }

    @Test
    public void getById() throws Exception {
        mvc.perform(get("/api/employees/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Bob Jones"));
    }

    @Test
    public void getMissingIs404() throws Exception {
        mvc.perform(get("/api/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void trailingSlashStillMatches() throws Exception {
        mvc.perform(get("/api/employees/")).andExpect(status().isOk());
    }

    @Test
    public void createUpdateDelete() throws Exception {
        String location = mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/employees/")))
                .andExpect(jsonPath("$.data.name").value("Carol"))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON)
                .content(VALID.replace("Carol", "Carol Updated")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Carol Updated"));

        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    public void invalidPayloadIs400() throws Exception {
        String bad = "{\"name\":\"\",\"email\":\"not-an-email\",\"department\":\"HR\",\"salary\":-1,\"hireDate\":\"2022-02-02\"}";
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(content().string(containsString("email")));
    }

    @Test
    public void futureHireDateIs400() throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
                .content(VALID.replace("2022-02-02", "2999-01-01")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void summaryByDepartment() throws Exception {
        mvc.perform(get("/api/employees/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].department").value("Engineering"))
                .andExpect(jsonPath("$.data[0].count").value(1));
    }

    @Test
    public void xmlExport() throws Exception {
        mvc.perform(get("/api/employees/1/xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<name>Alice Smith</name>")));
    }
}
