package com.lopes.order.infrastructure.persistence.entity;

import com.lopes.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade de pedido para persistência no MongoDB.
 * Inclui índice composto para prevenir duplicação de pedidos do mesmo cliente em curto período.
 */
@Document(collection = "orders")
@CompoundIndex(
    name = "idx_customer_created",
    def = "{'customerId': 1, 'createdAt': -1}",
    background = true
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    private String id;
    
    @Indexed
    private String customerId;
    
    private List<String> productIds;
    
    private BigDecimal totalValue;
    
    private String status;
    
    @Indexed
    private LocalDateTime createdAt;

    public OrderStatus getOrderStatus() {
        return OrderStatus.valueOf(status);
    }

    public void setOrderStatus(OrderStatus status) {
        this.status = status.name();
    }
}
