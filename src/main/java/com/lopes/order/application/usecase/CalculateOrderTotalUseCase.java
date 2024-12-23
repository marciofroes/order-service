package com.lopes.order.application.usecase;

import com.lopes.order.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateOrderTotalUseCase {
    private final ProductRepository productRepository;

    public record CalculateOrderTotalInput(List<String> productIds) {}
    public record CalculateOrderTotalOutput(BigDecimal total) {}

    public CalculateOrderTotalOutput execute(CalculateOrderTotalInput input) {
        var total = BigDecimal.ZERO;
        
        for (String productId : input.productIds()) {
            var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
            total = total.add(product.price());
        }
        
        return new CalculateOrderTotalOutput(total);
    }
}
