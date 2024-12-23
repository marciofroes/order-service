package com.lopes.order.adapters.rest.handler;

import com.lopes.order.domain.exception.PedidoDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PedidoDuplicadoException.class)
    public ResponseEntity<ErroResponse> tratarPedidoDuplicado(PedidoDuplicadoException ex) {
        var erro = new ErroResponse("PEDIDO_DUPLICADO", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    record ErroResponse(String codigo, String mensagem) {}
}
