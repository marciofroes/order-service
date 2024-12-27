package com.lopes.order.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMetricsTest {

    private MeterRegistry registry;
    private OrderMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new OrderMetrics(registry);
    }

    @Test
    void shouldIncrementOrderCreatedCounter() {
        // Act
        metrics.incrementOrderCreated();
        metrics.incrementOrderCreated();

        // Assert
        double count = registry.get("order.created.total").counter().count();
        assertThat(count).isEqualTo(2.0);
    }

    @Test
    void shouldIncrementOrderErrorCounter() {
        // Act
        metrics.incrementOrderError();

        // Assert
        double count = registry.get("order.errors.total").counter().count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test
    void shouldRecordOrderProcessingTime() {
        // Act
        metrics.recordOrderProcessingTime(100);
        metrics.recordOrderProcessingTime(200);

        // Assert
        double count = registry.get("order.processing.time").timer().count();
        assertThat(count).isEqualTo(2);
    }
}
