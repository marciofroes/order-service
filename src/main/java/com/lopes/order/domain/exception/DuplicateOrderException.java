package com.lopes.order.domain.exception;

/**
 * Exceção lançada quando é detectada uma tentativa de criar um pedido duplicado
 * dentro de uma janela de tempo específica para o mesmo cliente.
 */
public class DuplicateOrderException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Pedido duplicado detectado";
    
    public DuplicateOrderException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicateOrderException(String message) {
        super(message);
    }

    public DuplicateOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
