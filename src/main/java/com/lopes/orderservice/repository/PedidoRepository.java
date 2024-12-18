package com.lopes.orderservice.repository;

import com.lopes.orderservice.model.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PedidoRepository  extends MongoRepository<Pedido, String> {
}
