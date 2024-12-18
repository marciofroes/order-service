package com.lopes.orderservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;
    private String idPedidoExterno;
    private Double valorTotal;
    private StatusPedido status;
    private String dataCriacao;
}
