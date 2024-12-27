package com.lopes.order.adapters.rest;

import com.lopes.order.adapters.rest.dto.OrderRequestDTO;
import com.lopes.order.adapters.rest.dto.OrderResponseDTO;
import com.lopes.order.application.usecase.CreateOrderUseCase;
import com.lopes.order.application.usecase.GetOrderUseCase;
import com.lopes.order.application.usecase.ProcessOrderUseCase;
import com.lopes.order.domain.model.Order;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ProcessOrderUseCase processOrderUseCase;

    @PostMapping
    @Timed(value = "order.controller.create", description = "Time taken to create an order through the controller")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        log.debug("Creating order for customer: {}", request.getCustomerId());
        
        var input = new CreateOrderUseCase.CreateOrderInput(
            request.getCustomerId(),
            request.getProductIds()
        );

        var order = createOrderUseCase.execute(input);
        return ResponseEntity.ok(toResponseDTO(order));
    }

    @GetMapping("/{orderId}")
    @Timed(value = "order.controller.get", description = "Time taken to get an order through the controller")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderId) {
        log.debug("Getting order: {}", orderId);
        
        try {
            var input = new GetOrderUseCase.GetOrderInput(orderId);
            var order = getOrderUseCase.execute(input);
            return ResponseEntity.ok(toResponseDTO(order));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Order not found")) {
                log.debug("Order not found: {}", orderId);
                return ResponseEntity.notFound().build();
            }
            log.error("Error getting order: {}", orderId, e);
            throw e;
        }
    }

    @PostMapping("/{orderId}/process")
    @Timed(value = "order.controller.process", description = "Time taken to process an order through the controller")
    public ResponseEntity<OrderResponseDTO> processOrder(@PathVariable String orderId) {
        log.debug("Processing order: {}", orderId);
        
        try {
            var input = new ProcessOrderUseCase.ProcessOrderInput(orderId);
            var order = processOrderUseCase.execute(input);
            return ResponseEntity.ok(toResponseDTO(order));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Order not found")) {
                log.debug("Order not found: {}", orderId);
                return ResponseEntity.notFound().build();
            }
            log.error("Error processing order: {}", orderId, e);
            throw e;
        }
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
            order.id(),
            order.customerId(),
            order.productIds(),
            order.totalValue(),
            order.status(),
            order.createdAt()
        );
    }
}
