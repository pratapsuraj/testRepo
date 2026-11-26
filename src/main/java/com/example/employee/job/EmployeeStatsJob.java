package com.example.employee.job;

import java.util.concurrent.atomic.AtomicLong;

// LEGACY: javax.annotation.PostConstruct ships with the JDK 8 (module-less rt.jar) - no dependency needed.
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.employee.repository.EmployeeRepository;

@Component
public class EmployeeStatsJob {

    private static final Logger log = LoggerFactory.getLogger(EmployeeStatsJob.class);

    private final EmployeeRepository repository;
    private final AtomicLong cachedCount = new AtomicLong(-1);

    @Autowired
    public EmployeeStatsJob(EmployeeRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void init() {
        log.info("EmployeeStatsJob initialised");
    }

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void logStats() {
        log.info("Employee count = {}", repository.count());
    }

    @Async
    public void refreshStatsAsync() {
        cachedCount.set(repository.count());
    }

    public long getCachedCount() {
        return cachedCount.get();
    }
}
