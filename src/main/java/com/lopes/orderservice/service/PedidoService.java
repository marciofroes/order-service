package com.lopes.orderservice.service;

import com.lopes.orderservice.exception.BusinessException;
import com.lopes.orderservice.mapper.PedidoMapper;
import com.lopes.orderservice.model.Pedido;
import com.lopes.orderservice.model.PedidoCreateDTO;
import com.lopes.orderservice.model.PedidoResponseDTO;
import com.lopes.orderservice.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;

/**
 * Implementação dos serviços de pedido.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService implements PedidoCreationService, PedidoRetrievalService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public Pedido create(@Valid PedidoCreateDTO createDTO) {
        log.info("Iniciando criação de pedido com ID externo: {}", createDTO.getIdPedidoExterno());
        try {
            Pedido pedido = pedidoMapper.toPedido(createDTO);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);
            log.info("Pedido criado com sucesso. ID: {}", pedidoSalvo.getId());
            return pedidoSalvo;
        } catch (BusinessException e) {
            log.error("Erro de negócio ao criar pedido: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar pedido: {}", e.getMessage());
            throw new BusinessException("Erro ao processar o pedido: " + e.getMessage());
        }
    }

    @Override
    public PedidoResponseDTO findById(String id) {
        log.info("Buscando pedido por ID: {}", id);
        return pedidoRepository.findById(id)
                .map(pedidoMapper::toResponseDTO)
                .orElseThrow(() -> {
                    log.error("Pedido não encontrado para o ID: {}", id);
                    return new BusinessException("Pedido não encontrado");
                });
    }
}
