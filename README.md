# YourRents API

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

## Running the service in development

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

## Running the executable JAR

You need a PostgreSQL database running somewhere (local, remote, in a docker container, etc.).

For example, in your local machine, create the database (using the `postgres`` user):

```shell
postgres@yourhost:~$ createuser -P your_rents_api
Insert the password for the new role: <secret>
Confirm password: <secret>
postgres@yourhost:~$ createdb -O your_rents_api -E UTF-8 your_rents_api
```

Then, run the service using the `target/your-rents-api-0.0.1-SNAPSHOT.jar` file:

```shell
java -jar target/your-rents-api-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/your_rents_api \
  --spring.datasource.username=your_rents_api \
  --spring.datasource.password=<secret>
```

## Running the service with Docker

An image is available on Docker Hub: <https://hub.docker.com/r/yourrents/your-rents-api>.

If you need, or prefer, to rebuild the image locally by yourself, run the following command:

```shell
./mvnw spring-boot:build-image
```

The image contains just the application, not the database, that must be provided separately.

### Running the service with Docker Compose

A `docker-compose-yrs-api.yml` file is provided to run the service with Docker Compose:

```shell
docker compose -f docker-compose-yrs-api.yml up
```

Try the services at <http://localhost:8080/swagger-ui.html>.

### Running the service alone

If you have a PostgreSQL database running on your machine, you can run the service alone.

First, create the database (using the `postgres` user):

```shell
postgres@yourhost:~$ createuser -P your_rents_api
Insert the password for the new role: <secret>
Confirm password: <secret>
postgres@yourhost:~$ createdb -O your_rents_api -E UTF-8 your_rents_api
```

Of course, you also need to grant the access to the database.

For example, in the `postgresql.conf` file:

```properties
listen_addresses = '*'
```

And in the `pg_hba.conf` file:

```text
host    your_rents_api    your_rents_api    samenet    scram-sha-256
```

Then, run the service:

```shell
docker run --rm -it -p 8080:8080 --add-host host.docker.internal:host-gateway \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/your_rents_api \
  -e SPRING_DATASOURCE_USERNAME=your_rents_api \
  -e SPRING_DATASOURCE_PASSWORD=<secret> \
  yourrents/your-rents-api
```
