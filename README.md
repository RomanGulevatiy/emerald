# Emerald Project

[![CI Pipeline][ci-shield]][ci-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

Emerald project is a REST API service based on Spring Boot. The project is developed as a learning initiative to improve architecture skills and use best practices in backend development.

## Table Of Contents

1. [Built With](#built-with)
1. [Getting Started](#getting-started)
1. [API Documentation](#api-documentation)
1. [License](#license)

---

## Built With

- **Core:** Java 21, Spring Boot 3
- **Modules:** Spring MVC, Spring Data JPA, Validation, MapStruct, Lombok
- **Security:** Spring Security, JWT
- **Database:** PostgreSQL, Liquibase (Migrations)
- **Caching:** Redis
- **Documentation:** Springdoc OpenAPI
- **Containerization:** Docker & Docker Compose

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

[issues-shield]: https://img.shields.io/github/issues/RomanGulevatiy/emerald?label=Issues
[issues-url]: https://github.com/RomanGulevatiy/emerald/issues
[license-shield]: https://img.shields.io/github/license/RomanGulevatiy/emerald?label=License
[license-url]: https://github.com/RomanGulevatiy/emerald/blob/master/LICENSE
[ci-shield]: https://github.com/RomanGulevatiy/emerald/actions/workflows/ci-pipeline.yml/badge.svg
[ci-url]: https://github.com/RomanGulevatiy/emerald/actions/workflows/ci-pipeline.yml
