package com.lopes.order.domain.port;

import com.lopes.order.domain.model.Order;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

/**
 * Porta de saída para persistência de pedidos.
 * Define as operações necessárias para manipular pedidos no repositório.
 */
public interface OrderRepository {
    /**
     * Salva um pedido no repositório.
     *
     * @param order pedido a ser salvo
     * @return pedido salvo com possíveis modificações do repositório
     */
    Order save(Order order);

    /**
     * Busca um pedido pelo seu identificador.
     *
     * @param id identificador do pedido
     * @return pedido encontrado ou Optional vazio se não existir
     */
    Optional<Order> findById(String id);

    /**
     * Lista todos os pedidos.
     *
     * @return lista de pedidos
     */
    List<Order> findAll();

    /**
     * Remove um pedido pelo seu identificador.
     *
     * @param id identificador do pedido
     */
    void deleteById(String id);

    /**
     * Verifica se existe pedido para o cliente após uma determinada data.
     *
     * @param customerId identificador do cliente
     * @param after data de referência
     * @return true se existir pedido, false caso contrário
     */
    boolean existsByCustomerIdAndCreatedAtAfter(String customerId, LocalDateTime after);

    /**
     * Busca o pedido mais recente de um cliente criado após uma determinada data.
     *
     * @param customerId identificador do cliente
     * @param since data de referência
     * @return pedido mais recente ou Optional vazio se não existir
     */
    Optional<Order> findLatestByCustomerId(String customerId, LocalDateTime since);
}
