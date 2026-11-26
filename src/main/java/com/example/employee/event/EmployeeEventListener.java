package com.example.employee.event;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.employee.job.EmployeeStatsJob;

@Component
public class EmployeeEventListener {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventListener.class);

    private final AtomicInteger createdCount = new AtomicInteger();
    private final EmployeeStatsJob statsJob;

    @Autowired
    public EmployeeEventListener(EmployeeStatsJob statsJob) {
        this.statsJob = statsJob;
    }

    @EventListener
    public void onCreated(EmployeeCreatedEvent event) {
        log.info("Employee created: id={} name={}", event.getEmployee().getId(), event.getEmployee().getName());
        createdCount.incrementAndGet();
        statsJob.refreshStatsAsync();
    }

    public int getCreatedCount() {
        return createdCount.get();
    }
}
