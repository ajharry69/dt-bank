# DT Bank - Microservices Platform

This project implements a simplified banking platform using a microservices' architecture.
It includes services for managing customers, accounts, and cards, along with common cloud
infrastructure patterns like service discovery, configuration management, API gateway, and
security.

---

## Architecture Overview

The platform is composed of several Spring Boot microservices that communicate with each other, often via REST APIs
orchestrated through an API Gateway.

* **Customer Service:** Manages customer biographical data.
* **Account Service:** Manages customer bank accounts.
* **Card Service:** Manages customer cards (virtual and physical) linked to accounts.
* **Spring Cloud Gateway:** Acts as the single entry point for all client requests, handling routing, security, and
  other cross-cutting concerns.
* **Eureka Discovery Service:** Allows services to register and discover each other dynamically.
* **Spring Cloud Config Server:** Provides centralized configuration management for all microservices.
* **PostgreSQL:** The relational database used by each microservice for persistence.

---

## Microservices

Each microservice is a Spring Boot application built with Maven.

### Customer Service (`customer-service`)

* **Description:** Handles CRUD operations for customer's biographical data.
* **API Documentation:** Exposes Swagger UI at http://localhost:8080/swagger-ui.html and OpenAPI spec at `/v3/api-docs`.

### Account Service (`account-service`)

* **Description:** Manages customer bank account data.
* **API Documentation:** Exposes Swagger UI at http://localhost:8080/swagger-ui.html and OpenAPI spec at `/v3/api-docs`.

### Card Service (`card-service`)

* **Description:** Manages customer card data.
* **API Documentation:** Exposes Swagger UI at http://localhost:8080/swagger-ui.html and OpenAPI spec at `/v3/api-docs`.

---

## Infrastructure Services

These services support the microservices architecture.

### Spring Cloud Gateway (`gateway`)

* **Description:** Single entry point for all API requests. Handles routing to appropriate microservices and load
  balancing.
* **Port:** 8080
* **Configuration:** Fetches its configuration (including routes) from the Spring Cloud Config Server.

### Eureka Service Discovery (`eureka-server`)

* **Description:** Allows microservices to register themselves and discover other registered services dynamically.
  The Gateway uses Eureka to find downstream services.
* **Port:** 8888
* **Dashboard:** Accessible at http://localhost:8761

### Spring Cloud Config Server (`config-server`)

* **Description:** Provides centralized externalized configuration for all microservices and the gateway. Configurations
  are typically backed by a Git repository.
* **Port:** 8888

---

## Core Technologies

* **Java 24**
* **Spring Boot 3.4.5**
* **Spring Cloud 2024.0.1**
* **Spring Cloud Gateway (MVC version)**
* **Spring Cloud Netflix Eureka**
* **Spring Cloud Config**
* **OpenFeign:** For inter-service communication.
* **PostgreSQL:** As the RDBMS for each service.
* **RabbitMQ:** As the messaging queue for inter-service communication.
* **Maven:** For project build and dependency management.
* **Testcontainers:** For integration tests and running application services, providing ephemeral Docker containers for
  dependencies like PostgreSQL, RabbitMQ, etc.
* **JUnit 5:** For unit and integration testing.
* **Lombok:** To reduce boilerplate code.
* **MapStruct:** For DTO-entity mapping.
* **Springdoc OpenAPI (Swagger):** For API documentation.

---

## Prerequisites

* **Java 24 JDK** (or compatible)
* **Apache Maven 3.6+**
* **Docker**
* **Git** (for cloning the project)
* An IDE (e.g., IntelliJ IDEA, Eclipse, VS Code)

---

## Setup and Running the Platform

This section outlines how to run the entire platform locally.

### Starting core services

The following command starts all the services that are used internally by microservices.

```bash
docker compose up
```

### Build and Run Microservices

Locate and run the main applications referenced in the following list.

> Ensure Docker is running on your system and start the applications in the order they are listed in.
> 
> **Video reference:** https://drive.google.com/file/d/1PieCRJWxzD3R71tVUKQEufj_lphJWrlk/view

| Service             | Main Application                                                                                                                        |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `config-server`     | [ConfigServerApplication.java](config-server/src/main/java/com/github/ajharry69/config/server/ConfigServerApplication.java)             |
| `discovery-service` | [DiscoveryServiceApplication.java](discovery-service/src/main/java/com/github/ajharry69/discovery/DiscoveryServiceApplication.java)     |
| `customer-service`  | [TestCustomerServiceApplication.java](customer-service/src/test/java/com/github/ajharry69/customer/TestCustomerServiceApplication.java) |
| `account-service`   | [TestAccountServiceApplication.java](account-service/src/test/java/com/github/ajharry69/account/TestAccountServiceApplication.java)     |
| `card-service`      | [TestCardServiceApplication.java](card-service/src/test/java/com/github/ajharry69/card/TestCardServiceApplication.java)                 |
| `gateway`           | [GatewayApplication.java](gateway/src/main/java/com/github/ajharry69/gateway/GatewayApplication.java)                                   |

Wait for each service to start and register with Eureka (check Eureka dashboard) before starting dependent services or
the gateway.

---

## API Documentation (Swagger)

API documentation is generated using Springdoc OpenAPI.

* OpenAPI UI: http://localhost:8080/swagger-ui.html

---

## Testing

The project includes both unit and integration tests.

> **Video reference:** https://drive.google.com/file/d/1VxS-deQrexQ3mpeLVd9cLlRDgV9M42TP/view

### Unit Tests

* Located in `src/test/java` of each microservice.
* Use JUnit 5 and Mockito.

### Integration Tests (Testcontainers)

* Located in `src/test/java` of each microservice (e.g., `customer-service`, `card-service`, `account-service`).
* Leverage **Testcontainers** to spin up required external dependencies, such as a PostgreSQL database, for each test
  run.
  This ensures tests are isolated and run against a clean environment.
* The Testcontainers setup for each service starts all its required Docker containers.

---

## Further Development

* Enhance error handling and logging.
* Add more comprehensive integration tests covering inter-service communication.
* Implement distributed tracing (e.g., using Micrometer Tracing with Zipkin).
* Set up CI/CD pipelines.

