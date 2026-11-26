package com.example.employee.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Root (non-web) application context: data source, transactions, scheduling and the service layer. */
@Configuration
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = {
        "com.example.employee.repository",
        "com.example.employee.service",
        "com.example.employee.event",
        "com.example.employee.job"
})
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        // LEGACY: H2 1.4.x - lenient SQL parser (e.g. unquoted keywords such as VALUE / YEAR were accepted).
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("employeedb")
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
