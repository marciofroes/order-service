package com.lopes.orderservice.mapper;

import com.lopes.orderservice.model.Pedido;
import com.lopes.orderservice.model.PedidoCreateDTO;
import com.lopes.orderservice.model.PedidoResponseDTO;
import com.lopes.orderservice.model.StatusPedido;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Responsável pelo mapeamento entre DTOs e entidade Pedido.
 */
@Component
public class PedidoMapper {

    /**
     * Converte um DTO de criação para a entidade Pedido.
     */
    public Pedido toPedido(PedidoCreateDTO createDTO) {
        return Pedido.builder()
                .idPedidoExterno(createDTO.getIdPedidoExterno())
                .valorTotal(createDTO.getValorTotal())
                .status(StatusPedido.RECEBIDO)
                .dataCriacao(LocalDateTime.now().toString())
                .build();
    }

    /**
     * Converte uma entidade Pedido para DTO de resposta.
     */
    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .idPedidoExterno(pedido.getIdPedidoExterno())
                .valorTotal(pedido.getValorTotal())
                .status(pedido.getStatus())
                .dataCriacao(pedido.getDataCriacao())
                .build();
    }
}
