package com.lopes.order.domain.exception;

public class PedidoDuplicadoException extends RuntimeException {
    public PedidoDuplicadoException(String message) {
        super(message);
    }
}
