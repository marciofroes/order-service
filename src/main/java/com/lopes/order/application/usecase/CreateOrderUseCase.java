package com.lopes.order.application.usecase;

import com.lopes.order.domain.model.Order;
import com.lopes.order.domain.model.OrderStatus;
import com.lopes.order.domain.repository.OrderRepository;
import com.lopes.order.domain.exception.PedidoDuplicadoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final CalculateOrderTotalUseCase calculateOrderTotalUseCase;

    public record CreateOrderInput(String customerId, List<String> productIds) {}
    public record CreateOrderOutput(String orderId) {}

    public CreateOrderOutput execute(CreateOrderInput input) {
        validarPedidoDuplicado(input);
        
        var calculateInput = new CalculateOrderTotalUseCase.CalculateOrderTotalInput(input.productIds());
        var valorTotal = calculateOrderTotalUseCase.execute(calculateInput).total();

        var pedido = new Order(
            UUID.randomUUID().toString(),
            input.customerId(),
            input.productIds(),
            valorTotal,
            OrderStatus.PENDING
        );

        orderRepository.save(pedido);
        return new CreateOrderOutput(pedido.id());
    }

    private void validarPedidoDuplicado(CreateOrderInput input) {
        if (orderRepository.existePedidoClienteProdutos(input.customerId(), input.productIds())) {
            throw new PedidoDuplicadoException("Já existe um pedido para este cliente com os mesmos produtos");
        }
    }
}
