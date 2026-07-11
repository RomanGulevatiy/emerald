# Emerald Project

[![Issues][issues-shield]][issues-url]
[![Build][ci-shield]][ci-url]
[![MIT License][license-shield]][license-url]

Emerald project is a REST API service based on Spring Boot. The project is developed as a learning initiative to improve architecture skills and use best practices in backend development.

## Table Of Contents

1. [Built With](#built-with)
1. [Getting Started](#getting-started)
1. [API Documentation](#api-documentation)
1. [License](#license)

---

## Built With

| Core          | Modules         | Security        | Database   | Caching | Documentation     | Containerization |
|---------------|-----------------|-----------------|------------|---------|-------------------|------------------|
| Java 21       | Spring MVC      | Spring Security | PostgreSQL | Redis   | Springdoc OpenAPI | Docker           |
| Spring Boot 3 | Spring Data JPA | JWT             | Liquibase  |         |                   | Docker Compose   |
|               | Validation      |                 |            |         |                   |                  |
|               | MapStruct       |                 |            |         |                   |                  |
|               | Lombok          |                 |            |         |                   |                  |

---

## Getting Started

### Prerequisites

Make sure you have the following installed on your system:
- Docker
- Docker Compose >=5.3.0

### Installations

#### 1. Clone the repository

```bash
git clone https://github.com/RomanGulevatiy/emerald.git
```

#### 2. Environment setup

Copy the template file to create your local .env file and adjust the values if needed:
```bash
cp .env.template .env
```

#### 3. Launch the application

Run the following command to build the multi-stage Docker image and start all services (API, PostgreSQL, Redis):
```bash
docker compose up --build
```

The API will be available at http://localhost:8080/api.

---

## API Documentation

The API documentation for this application is available at http://localhost:8080/api/swagger-ui/index.html. It details all endpoints and their usage.

---

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.

[issues-shield]: https://img.shields.io/github/issues/RomanGulevatiy/emerald
[issues-url]: https://github.com/RomanGulevatiy/emerald/issues
[license-shield]: https://img.shields.io/github/license/RomanGulevatiy/emerald
[license-url]: https://github.com/RomanGulevatiy/emerald/blob/master/LICENSE
[ci-shield]: https://img.shields.io/github/actions/workflow/status/RomanGulevatiy/emerald/maven.yml
[ci-url]: https://github.com/RomanGulevatiy/emerald/actions/workflows/maven.yml
