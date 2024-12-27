package com.lopes.order.domain.model;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Representa um pedido no domínio.
 * Este record é imutável e contém todas as informações necessárias de um pedido.
 */
public record Order(
    String id,
    String customerId,
    List<String> productIds,
    BigDecimal totalValue,
    OrderStatus status,
    LocalDateTime createdAt
) {
    /**
     * Cria um novo pedido com valores padrão.
     *
     * @param customerId identificador do cliente
     * @param productIds lista de identificadores dos produtos
     * @param totalValue valor total do pedido
     * @param clock relógio para controle de tempo
     * @return novo pedido criado
     */
    public static Order create(
        String customerId,
        List<String> productIds,
        BigDecimal totalValue,
        Clock clock
    ) {
        return new Order(
            UUID.randomUUID().toString(),
            customerId,
            productIds,
            totalValue,
            OrderStatus.CREATED,
            LocalDateTime.now(clock)
        );
    }

    /**
     * Processa o pedido, alterando seu status para PROCESSED.
     * O pedido deve estar no status CREATED para ser processado.
     *
     * @return novo pedido processado
     * @throws IllegalStateException se o pedido não estiver no status CREATED
     */
    public Order process() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Order must be in CREATED status to be processed");
        }
        return new Order(
            id,
            customerId,
            productIds,
            totalValue,
            OrderStatus.PROCESSED,
            createdAt
        );
    }
}
