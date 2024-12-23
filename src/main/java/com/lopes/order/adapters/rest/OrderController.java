package com.lopes.order.adapters.rest;

import com.lopes.order.adapters.rest.dto.OrderRequestDTO;
import com.lopes.order.adapters.rest.dto.OrderResponseDTO;
import com.lopes.order.application.usecase.CreateOrderUseCase;
import com.lopes.order.application.usecase.GetOrderUseCase;
import com.lopes.order.application.usecase.ProcessOrderUseCase;
import com.lopes.order.domain.model.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ProcessOrderUseCase processOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        var input = CreateOrderUseCase.CreateOrderInput.builder()
            .customerId(request.getCustomerId())
            .productIds(request.getProductIds())
            .build();

        var output = createOrderUseCase.execute(input);
        
        var order = getOrderUseCase.execute(GetOrderUseCase.GetOrderInput.builder()
            .orderId(output.orderId())
            .build());

        return ResponseEntity.ok(toResponseDTO(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderId) {
        var output = getOrderUseCase.execute(GetOrderUseCase.GetOrderInput.builder()
            .orderId(orderId)
            .build());

        return ResponseEntity.ok(toResponseDTO(output));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus newStatus) {
        var output = processOrderUseCase.execute(ProcessOrderUseCase.ProcessOrderInput.builder()
            .orderId(orderId)
            .newStatus(newStatus)
            .build());

        return ResponseEntity.ok(toResponseDTO(output));
    }

    private OrderResponseDTO toResponseDTO(GetOrderUseCase.GetOrderOutput output) {
        return OrderResponseDTO.builder()
            .id(output.id())
            .customerId(output.customerId())
            .productIds(output.productIds())
            .totalValue(output.totalValue())
            .status(output.status())
            .build();
    }

    private OrderResponseDTO toResponseDTO(ProcessOrderUseCase.ProcessOrderOutput output) {
        return OrderResponseDTO.builder()
            .id(output.id())
            .customerId(output.customerId())
            .productIds(output.productIds())
            .totalValue(output.totalValue())
            .status(output.status())
            .build();
    }
}
