package com.lopes.order.adapters.rest;

import com.lopes.order.application.usecase.ProcessOrderUseCase;
import com.lopes.order.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final ProcessOrderUseCase processOrderUseCase;

    public OrderController(ProcessOrderUseCase processOrderUseCase) {
        this.processOrderUseCase = processOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> criarPedido(@RequestBody Order order) {
        processOrderUseCase.processarPedido(order);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
