# notification-service

# Serviço de Notificação - Backend

## Introdução

Este microsserviço é responsável por receber eventos de encomendas (parcel events), criar e enviar notificações aos moradores (e-mail, SMS, PUSH), 
persistir o histórico de notificações e publicar eventos de saída em Kafka para integracao com outros serviços.

## Objetivo do Projeto

Fornecer uma API e pipeline de processamento robustos para:
- Receber eventos de encomenda via Kafka;
- Construir e enviar notificações via provedores (mock no ambiente local);
- Persistir o histórico de notificações (PostgreSQL);
- Publicar eventos de notificação (NOTIFICATION_SENT) em Kafka;
- Permitir consulta e confirmação (ack) de notificações por ID.

## Requisitos do Sistema

- Sistema Operacional: Windows, macOS ou Linux
- Memória RAM: Pelo menos 4 GB recomendados
- Espaço em Disco: Pelo menos 500 MB de espaço livre
- Software:
    - Docker e Docker Compose
    - Java JDK 11 ou superior
    - Maven 3.6 ou superior
    - PostgreSQL (pode ser via container)
    - Kafka (pode ser via container)
    - Git

## Estrutura do Projeto

A estrutura principal do projeto segue o package `com.fiap.notificationservice` e está organizada de forma a separar adaptações (adapters), casos de uso e infraestrutura:

```plaintext
notification-service/
│
├── src/
│ └── main/
│   ├── java/
│   │ └── com.fiap.notificationservice
│   │   ├── adapter/
│   │   │   └── web/ : Controllers e mappers para API REST
│   │   ├── application/
│   │   │   ├── dto/ : DTOs de entrada/saída (ParcelEventDto, NotificationResponseDto)
│   │   │   └── usecase/ : Casos de uso (ProcessParcelEventUseCase, AcknowledgeNotificationUseCase)
│   │   ├── domain/ : Modelo de domínio (Notification)
│   │   ├── domain/port/ : Portas (NotificationRepository, NotificationSender)
│   │   ├── infrastructure/
│   │   │   ├── kafka/ : Producer, listeners e configuração Kafka
│   │   │   ├── persistence/ : Entities JPA, Repository adapter e mappers
│   │   │   ├── provider/ : Adapters para provedores (MockProvider, NotificationSenderAdapter)
│   │   │   ├── config/ : Configurações (Swagger/OpenAPI, Jackson, Security)
│   │   │   └── messaging/ : Listeners auxiliares
│   │   └── NotificationServiceApplication.java : Classe principal
│   └── resources/
│       └── application.properties : Configurações da aplicação (variáveis podem vir do ambiente)
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Principais Componentes

- NotificationService: processa payloads (Map) recebidos e cria Notification domain object.
- ProcessParcelEventUseCase: constrói e envia Notification a partir de ParcelEventDto.
- AcknowledgeNotificationUseCase: marca notificações como acknowledged e atualiza status/sentAt.
- NotificationSenderAdapter: integra com provider (atualmente MockProvider), persiste e publica evento.
- NotificationProducer: publica mensagens em Kafka.
- NotificationRepositoryAdapter: implementa persistência via Spring Data JPA.
- Kafka listeners: consomem tópicos de entrada (parcels) e delegam aos casos de uso.

## Arquitetura

Seguindo práticas de arquitetura limpa simplificada:
- Controller/adapters (API + mappers)
- UseCases (regras e orquestração)
- Domain (modelo de negócio)
- Ports/Gateways (interfaces e implementações de persistência/entrega)
- Infrastructure (Kafka, JPA, provedores mock, configuração)

## Segurança

- A aplicação contém configurações de Spring Security para proteger endpoints de produção.
- Para fins de documentação e execução local, rotas do Swagger/OpenAPI são permitidas sem autenticação nas configurações específicas.
- O endpoint de ack possui uma anotação de autorização: `@PreAuthorize("@securityService.canAcknowledgeNotification(#id.toString(), authentication)")`.

## Tópicos Kafka e Variáveis de Ambiente

Valores padrão e variáveis encontradas no código:
- Tópico de entrada (parcels):
    - `kafka.topics.parcels-in` (ex.: `parcels-in`) usado pelo `ParcelEventListener`.
    - Algumas partes usam `KAFKA_TOPICS_PARCELS_IN` com fallback `parcels.received` (ver `NotificationKafkaListener`).
- Tópico de saída (notifications):
    - `kafka.topics.notifications-out` (ex.: `notifications-out`) usado pelo `NotificationProducer`.
    - Em algumas publicações auxiliares é usado `KAFKA_TOPICS_NOTIFICATIONS_OUT` com fallback `notifications.sent`.
- Bootstrap Kafka:
    - `SPRING_KAFKA_BOOTSTRAP_SERVERS` ou `KAFKA_BOOTSTRAP_SERVERS` (default: `kafka:9092`).
- Grupo de consumidor:
    - `KAFKA_CONSUMER_GROUP_ID` ou `spring.kafka.consumer.group-id` (default: `notification-service`).
- External providers mock:
    - `EXTERNAL_PROVIDERS_MOCK` (default: `http://mock-providers`) — usado para simular chamadas a `/sms` e `/email`.

## Banco de Dados

- Entidade JPA: `notifications` (classe `NotificationEntity`).
- Colunas principais: id (UUID), parcel_id, resident_name, apartment, contact, channel, message, description, status, result_detail, created_at, sent_at, updated_at, acknowledged.
- Repositório JPA: `SpringDataNotificationRepository` e adapter `NotificationRepositoryAdapter`.

## Endpoints Principais

- POST /api/notification/{id}/ack — Confirmar (ack) notificação por ID (retorna NotificationResponseDto)
    - Regras: atualiza acknowledged, atualiza status para SENT se estiver PENDING/null, popula sentAt se necessário.
- GET /api/notification/{id} — Obter notificação por ID (retorna NotificationResponseDto)

## Como Executar com Docker Compose (exemplo)

1. Certifique-se que Docker e Docker Compose estão instalados.
2. Ajuste `docker-compose.yml` para incluir serviços: app (notification-service), postgres, kafka, zookeeper (ou use uma stack Kafka pronta) e adminer.
3. No diretório do projeto, execute:
   ```bash
   docker compose up --build
   ```
4. A aplicação Spring Boot estará disponível em `http://localhost:8083` .
5. Swagger em `http://localhost:8083/swagger-ui/index.html#/`.
6. PostgreSQL em `localhost:5432`, Adminer em `http://localhost:8088`.

Recomendações de ambiente (exemplos):
- SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
- SPRING_DATASOURCE_USERNAME=postgres
- SPRING_DATASOURCE_PASSWORD=postgres
- SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
- KAFKA_TOPICS_PARCELS_IN=parcels-in
- KAFKA_TOPICS_NOTIFICATIONS_OUT=notifications.sent

## Testes e Simulações

- Em ambiente local o `MockProvider` faz apenas logs do envio. Para testar envio real, substitua ou adapte `NotificationSenderAdapter` para integrar com provedores reais.
- Para gerar eventos de entrada, publique mensagens JSON no tópico de parcels configurado ou use o `ParcelEventDto` format:
  ```json
  {
    "parcelId": 123,
    "residentName": "Fulano",
    "apartment": "A101",
    "contact": "email@exemplo.com",
    "channel": "EMAIL",
    "description": "Pacote Amazon",
    "receivedAt": "2025-01-01T12:00:00Z"
  }
  ```

## Tecnologias Utilizadas

- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL
- Apache Kafka (Spring Kafka)
- Jackson (configurada para java.time)
- MapStruct 
- Lombok (opcional conforme uso)
- Swagger/OpenAPI (springdoc)
- Docker e Docker Compose

## Contribuição

1. Faça um fork do repositório.
2. Crie uma branch (`git checkout -b feature/nome-da-feature`).
3. Faça commits claros (`git commit -m 'Descrição'`).
4. Abra um Pull Request.

## Licença

Projeto privado (sem licença específica).

## Referências

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Kafka: https://spring.io/projects/spring-kafka
- Jackson JavaTime: https://github.com/FasterXML/jackson-modules-java8

## Conclusão
Este microsserviço de auditoria é crucial para garantir que todos os eventos relevantes sejam rastreados e registrados, proporcionando uma visão clara e consistente da atividade no sistema.
Este serviço de notificação fornece um pipeline simples e extensível para receber eventos de encomendas, gerar e enviar notificações aos moradores, registrar o histórico em banco de dados e publicar eventos de saída em Kafka. Ele foi projetado seguindo uma arquitetura orientada a casos de uso e portas/adapters, o que facilita a substituição de adaptadores (por exemplo, integrar provedores reais de e-mail/SMS/Push) sem impactar a lógica de negócio.
