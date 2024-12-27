# Serviço de Pedidos (Order Service)

Este é um serviço de gerenciamento de pedidos construído seguindo os princípios do Clean Architecture, utilizando Spring Boot e MongoDB.

## Arquitetura

O projeto segue a Clean Architecture, com as seguintes camadas:

### 1. Domain (Núcleo)
- `domain/model`: Entidades de domínio (ex: `Order`)
- `domain/port`: Interfaces que definem contratos (ex: `OrderRepository`)
- `domain/exception`: Exceções de domínio

### 2. Application (Casos de Uso)
- `application/usecase`: Implementação dos casos de uso (ex: `CreateOrderUseCase`)
- Orquestra as regras de negócio usando as entidades do domínio

### 3. Infrastructure (Adaptadores)
- `infrastructure/persistence`: Implementação da persistência
  - `entity`: Entidades JPA/MongoDB
  - `repository`: Implementações dos repositórios
  - `mapper`: Conversores entre domínio e persistência
- `infrastructure/metrics`: Métricas e monitoramento

### 4. Adapters (Interface)
- `adapters/rest`: Controllers e DTOs
- `adapters/client`: Clientes para serviços externos

## Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker (para MongoDB)
- Docker Compose

### Configuração do Docker
1. **Instalação do Docker Desktop**
   - Baixe e instale o [Docker Desktop](https://www.docker.com/products/docker-desktop/)
   - Durante a instalação, certifique-se de que o WSL 2 está habilitado
   - Inicie o Docker Desktop e aguarde até estar rodando (ícone verde)

2. **Verificação da Instalação**
   ```bash
   # Verifique a versão do Docker
   docker --version

   # Verifique se o daemon está rodando
   docker ps
   ```

3. **Configuração do WSL 2 (Windows)**
   ```bash
   # Verifique o status do WSL
   wsl --status

   # Atualize se necessário
   wsl --update
   ```

### Executando a Aplicação
1. Clone o repositório
2. Execute o MongoDB:
   ```bash
   docker-compose up -d
   ```
3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

## Testes

O projeto possui diferentes níveis de testes:

### Testes Unitários
```bash
mvn test
```

### Testes de Integração
```bash
mvn verify
```

Os testes de integração utilizam:
- TestContainers para MongoDB
- WireMock para simular serviços externos

### Configuração do Ambiente de Testes
1. **Certifique-se que o Docker está Rodando**
   - O Docker Desktop deve estar ativo
   - O daemon do Docker deve estar respondendo
   - Portas necessárias devem estar livres (27017 para MongoDB)

2. **TestContainers**
   - Usado para testes de integração
   - Cria containers Docker automaticamente
   - Gerencia ciclo de vida dos containers

### Executando os Testes

1. **Testes Unitários (Sem Docker)**
   ```bash
   mvn test -Dtest=*Test
   ```

2. **Testes de Integração (Com Docker)**
   ```bash
   # Certifique-se que o Docker está rodando
   mvn test -Dtest=*IntegrationTest

   # Ou todos os testes
   mvn verify
   ```

3. **Testes Específicos**
   ```bash
   mvn test -Dtest=OrderIntegrationTest
   ```

### Troubleshooting de Testes

1. **Erro "Docker environment not found"**
   - Verifique se Docker Desktop está rodando
   - Reinicie o Docker Desktop
   - Reinicie sua IDE

2. **Erro de Conexão MongoDB**
   - Verifique se porta 27017 está livre
   - Verifique logs do Docker
   ```bash
   docker ps
   docker logs <container-id>
   ```

3. **Problemas com WSL 2**
   - Verifique status: `wsl --status`
   - Atualize WSL: `wsl --update`
   - Reinicie WSL: `wsl --shutdown`

4. **Limpeza de Recursos**
   ```bash
   # Remove containers parados
   docker container prune

   # Remove todos os containers
   docker rm -f $(docker ps -aq)
   ```

## Convenções e Padrões

### 1. Clean Architecture
- Dependências apontam para dentro
- Domínio é independente de frameworks
- DTOs não atravessam camadas internas

### 2. Records vs Classes
- Records: Para objetos imutáveis do domínio
- Classes: Para entidades de persistência e DTOs

### 3. Lombok
- `@Data`: Para DTOs e entidades
- `@Builder`: Para construção fluente
- `@AllArgsConstructor`/`@NoArgsConstructor`: Quando necessário

### 4. Testes
- Given/When/Then ou Arrange/Act/Assert
- Um assert por comportamento
- Usar builders nos testes

## Principais Funcionalidades

### 1. Criar Pedido
```http
POST /orders
{
    "customerId": "customer123",
    "productIds": ["prod1", "prod2"]
}
```

### 2. Consultar Pedido
```http
GET /orders/{orderId}
```

### 3. Listar Pedidos
```http
GET /orders
```

## Manutenção e Desenvolvimento

### 1. Adicionar Nova Funcionalidade
1. Crie/atualize entidade no domínio
2. Implemente caso de uso
3. Atualize adaptadores necessários
4. Adicione testes em todos os níveis

### 2. Modificar Persistência
1. Atualize `OrderEntity`
2. Modifique `OrderMapper`
3. Atualize testes de integração

### 3. Adicionar Endpoint
1. Crie DTO se necessário
2. Implemente controller
3. Adicione testes de integração

## Monitoramento

- Métricas disponíveis em `/actuator/metrics`
- Saúde da aplicação em `/actuator/health`
- Logs estruturados em JSON

## Segurança

- Validação de entrada com Bean Validation
- Sanitização de dados
- Tratamento adequado de exceções

## Documentação Adicional

- Swagger UI: `/swagger-ui.html`
- OpenAPI: `/v3/api-docs`

## Práticas a Evitar

1. Não atravesse camadas com DTOs ou entidades
2. Não adicione lógica de negócio em controllers
3. Não use entidades de persistência no domínio
4. Não quebre a imutabilidade do domínio

## Contribuindo

1. Crie uma branch: `feature/nome-feature`
2. Faça commits semânticos
3. Abra Pull Request com descrição clara
4. Aguarde review

## Suporte

- Documentação: `/docs`
- Wiki: [Link para wiki]
- Time: [Contatos]