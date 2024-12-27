package com.lopes.order.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderMetrics {

    private final Counter orderCreatedCounter;
    private final Counter orderErrorCounter;
    private final Timer orderProcessingTimer;

    public OrderMetrics(MeterRegistry registry) {
        this.orderCreatedCounter = Counter.builder("order.created.total")
            .description("Total number of orders created")
            .register(registry);

        this.orderErrorCounter = Counter.builder("order.errors.total")
            .description("Total number of order processing errors")
            .register(registry);

        this.orderProcessingTimer = Timer.builder("order.processing.time")
            .description("Time taken to process orders")
            .register(registry);
    }

    public void incrementOrderCreated() {
        orderCreatedCounter.increment();
        log.debug("Order created counter incremented");
    }

    public void incrementOrderError() {
        orderErrorCounter.increment();
        log.debug("Order error counter incremented");
    }

    public void recordOrderProcessingTime(long timeInMs) {
        orderProcessingTimer.record(timeInMs, TimeUnit.MILLISECONDS);
        log.debug("Order processing time recorded: {}ms", timeInMs);
    }

    public Timer getOrderProcessingTimer() {
        return orderProcessingTimer;
    }
}
