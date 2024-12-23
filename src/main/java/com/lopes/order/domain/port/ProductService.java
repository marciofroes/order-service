package com.lopes.order.domain.port;

import com.lopes.order.domain.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getProductsByIds(List<String> productIds);
}
