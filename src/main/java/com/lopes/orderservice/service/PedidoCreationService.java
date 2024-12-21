package com.lopes.orderservice.service;

import com.lopes.orderservice.model.Pedido;
import com.lopes.orderservice.model.PedidoCreateDTO;
import jakarta.validation.Valid;

/**
 * Serviço responsável pela criação de pedidos.
 */
public interface PedidoCreationService {
    /**
     * Cria um novo pedido.
     *
     * @param createDTO DTO contendo os dados do pedido
     * @return Pedido criado
     * @throws BusinessException se houver erro de validação ou processamento
     */
    Pedido create(@Valid PedidoCreateDTO createDTO);
}