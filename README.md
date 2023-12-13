# your-rents-api

Backend API for the YourRents applications

[![Build](https://github.com/your-rents/your-rents-api/actions/workflows/maven.yml/badge.svg)](https://github.com/your-rents/your-rents-api/actions/workflows/maven.yml)

## Prerequisites

- Java 21+
- A Docker environment supported by Testcontainers: <https://www.testcontainers.org/supported_docker_environment/> (a recent local Docker installation is enough)

## Getting Started

Clone the repository and run the following command to build the project:

```shell
./mvnw clean verify
```

## Running the service

Run the following command:

```shell
./mvnw spring-boot:run
```

Try the services at <http://localhost:8080/swagger-ui.html>.

You can inspect the database using Adminer at <http://localhost:38080> with the following connection parameters:

- **System:** PostgreSQL
- **Server:** postgres-yrs-api
- **Username:** your_rents_api
- **Password:** your_rents_api
- **Database:** your_rents_api

## Cleaning the database

If you need a fresh database:

- stop the service
- remove the Docker containers:

     ```shell
     docker compose down
     ```

- Remove the `your-rents-api_postgres-yrs-api` volume:

     ```shell
     docker volume rm your-rents-api_postgres-yrs-api
     ```

- Then restart the service
