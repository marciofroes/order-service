
package com.lopes.orderservice.model;

import com.lopes.orderservice.exception.BusinessException;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;
    private String idPedidoExterno;
    private Double valorTotal;
    private StatusPedido status;
    private String dataCriacao;

    private Pedido(PedidoBuilder builder) {
        this.id = builder.id;
        this.idPedidoExterno = builder.idPedidoExterno;
        this.valorTotal = builder.valorTotal;
        this.status = builder.status;
        this.dataCriacao = builder.dataCriacao;
    }

    public static PedidoBuilder builder() {
        return new PedidoBuilder();
    }

    public static class PedidoBuilder {
        private String id;
        private String idPedidoExterno;
        private Double valorTotal;
        private StatusPedido status;
        private String dataCriacao;

        public PedidoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PedidoBuilder idPedidoExterno(String idPedidoExterno) {
            if (idPedidoExterno == null || idPedidoExterno.trim().isEmpty()) {
                throw new BusinessException("ID do pedido externo não pode ser vazio");
            }
            this.idPedidoExterno = idPedidoExterno;
            return this;
        }

        public PedidoBuilder valorTotal(Double valorTotal) {
            if (valorTotal == null || valorTotal <= 0) {
                throw new BusinessException("Valor total deve ser maior que zero");
            }
            this.valorTotal = valorTotal;
            return this;
        }

        public PedidoBuilder status(StatusPedido status) {
            if (status == null) {
                throw new BusinessException("Status do pedido não pode ser nulo");
            }
            this.status = status;
            return this;
        }

        public PedidoBuilder dataCriacao(String dataCriacao) {
            if (dataCriacao == null || dataCriacao.trim().isEmpty()) {
                throw new BusinessException("Data de criação não pode ser vazia");
            }
            this.dataCriacao = dataCriacao;
            return this;
        }

        public Pedido build() {
            validarPedido();
            return new Pedido(this);
        }

        private void validarPedido() {
            if (valorTotal == null || valorTotal <= 0) {
                throw new BusinessException("Valor total inválido");
            }
            if (idPedidoExterno == null || idPedidoExterno.trim().isEmpty()) {
                throw new BusinessException("ID do pedido externo é obrigatório");
            }
            if (status == null) {
                throw new BusinessException("Status do pedido é obrigatório");
            }
            if (dataCriacao == null || dataCriacao.trim().isEmpty()) {
                throw new BusinessException("Data de criação é obrigatória");
            }
        }
    }
}