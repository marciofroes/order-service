# Order Service

Este é um microsserviço responsável pelo gerenciamento de pedidos (orders), desenvolvido seguindo as melhores práticas de arquitetura limpa e padrões de projeto.

## Arquitetura do Projeto

O projeto segue uma arquitetura hexagonal (também conhecida como Ports and Adapters) com Clean Architecture, organizada nas seguintes camadas:

### 1. Domain (Camada de Domínio)
- Localização: `domain/`
- Contém as regras de negócio centrais
- Classes principais:
  - `Order`: Record que representa o modelo de domínio de um pedido
  - `OrderStatus`: Enum com os possíveis estados de um pedido

### 2. Application (Camada de Aplicação)
- Localização: `application/`
- Orquestra os casos de uso da aplicação
- Implementa a lógica de negócio usando os modelos de domínio

### 3. Infrastructure (Camada de Infraestrutura)
- Localização: `infrastructure/`
- Contém implementações técnicas como:
  - Persistência de dados (JPA)
  - `OrderEntity`: Entidade JPA para mapeamento do banco de dados
  - `JpaOrderRepository`: Repositório Spring Data JPA

### 4. Adapters (Adaptadores)
- Localização: `adapters/`
- Adaptadores para comunicação externa
- Inclui controllers REST, consumidores de mensagens, etc.
