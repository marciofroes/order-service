package com.lopes.orderservice.service;

import com.lopes.orderservice.model.PedidoResponseDTO;

/**
 * Serviço responsável pela recuperação de pedidos.
 */
public interface PedidoRetrievalService {
    /**
     * Busca um pedido pelo seu ID.
     *
     * @param id ID do pedido
     * @return DTO com os dados do pedido
     * @throws BusinessException se o pedido não for encontrado
     */
    PedidoResponseDTO findById(String id);
}
