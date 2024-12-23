package com.lopes.order.domain.model;

public enum OrderStatus {
    PENDING,      // Pedido criado, aguardando processamento
    PROCESSING,   // Pedido em processamento
    COMPLETED,    // Pedido concluído com sucesso
    CANCELLED     // Pedido cancelado
}
