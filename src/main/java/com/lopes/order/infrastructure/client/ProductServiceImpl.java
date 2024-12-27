package com.lopes.order.infrastructure.client;

import com.lopes.order.domain.model.Product;
import com.lopes.order.domain.port.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductServiceImpl(
            RestTemplate restTemplate,
            @Value("${product.service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public List<Product> getProductsByIds(List<String> productIds) {
        return productIds.stream()
                .map(id -> restTemplate.getForObject(productServiceUrl + "/products/" + id, Product.class))
                .toList();
    }

    @Override
    public BigDecimal calculateTotal(List<String> productIds) {
        List<Product> products = getProductsByIds(productIds);
        return products.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
