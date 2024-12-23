package com.lopes.order.infrastructure.client;

import com.lopes.order.domain.model.Product;
import com.lopes.order.domain.port.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final RestTemplate restTemplate;
    private final String productServiceUrl = "http://product-service/products"; // Configure in properties

    @Override
    public List<Product> getProductsByIds(List<String> productIds) {
        return productIds.stream()
            .map(id -> restTemplate.getForObject(productServiceUrl + "/" + id, Product.class))
            .collect(Collectors.toList());
    }
}
