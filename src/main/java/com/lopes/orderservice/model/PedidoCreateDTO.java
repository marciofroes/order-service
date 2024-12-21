package com.lopes.orderservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO específico para criação de pedidos
 */
@Data
public class PedidoCreateDTO {
    @NotBlank(message = "ID do pedido externo é obrigatório")
    private String idPedidoExterno;
    
    @NotNull(message = "Valor total é obrigatório")
    @Positive(message = "Valor total deve ser maior que zero")
    private Double valorTotal;
}
