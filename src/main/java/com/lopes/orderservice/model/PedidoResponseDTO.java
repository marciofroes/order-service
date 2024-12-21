package com.lopes.orderservice.model;

import lombok.Builder;
import lombok.Data;

/**
 * DTO específico para respostas de pedidos
 */
@Data
@Builder
public class PedidoResponseDTO {
    private String id;
    private String idPedidoExterno;
    private Double valorTotal;
    private StatusPedido status;
    private String dataCriacao;
}
