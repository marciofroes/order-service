package com.lopes.order.application.usecase;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.model.OrderStatus;
import com.lopes.order.domain.model.Product;
import com.lopes.order.domain.port.OrderRepository;
import com.lopes.order.domain.port.ProductService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Builder
    public record CreateOrderInput(
        String customerId,
        List<String> productIds
    ) {}

    @Builder
    public record CreateOrderOutput(
        String orderId
    ) {}

    public CreateOrderOutput execute(CreateOrderInput input) {
        // Validate products exist and calculate total
        List<Product> products = productService.getProductsByIds(input.productIds());
        BigDecimal total = products.stream()
            .map(Product::price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order(
            UUID.randomUUID().toString(),
            input.customerId(),
            input.productIds(),
            total,
            OrderStatus.PENDING
        );

        // Save and return
        Order savedOrder = orderRepository.save(order);
        return new CreateOrderOutput(savedOrder.id());
    }
}
