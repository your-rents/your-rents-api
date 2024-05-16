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

A `docker-compose-yrs-api.yaml` file is provided to run the service with Docker Compose:

```shell
docker compose -f docker-compose-yrs-api.yaml up
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

## Calling the service

The service is protected by an API key, using Keycloak for authentication and authorization.

For simplicity, assign a domain name to the KeyCloak server, for example `keycloak.local`, and add the following line to your `/etc/hosts` file:

```text
127.0.0.1 localhost keycloak.local
```

The Keycloak server is available at <http://keycloak.local:18080>. You can access the administration console with the `admin` user and the `Pa55w0rd` password.

### Calling the service in development

In development, launching the service with `./mvnw spring-boot:run` (or with the plugins of your IDE), the service environment is launched using docker compose with the `compose.yml` file.

#### Using `curl`

Run the following command to get an access token:

```shell
TOKEN=$(curl -X POST \
  http://localhost:18080/realms/your-rents/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d username=user \
  -d password=user \
  -d grant_type=password \
  -d client_id=your-rents-api \
  -d client_secret=ZEeQ3Zmhnm3NX6QPGlEpPOLB2OavM3GZ \
  | jq -r .access_token)
```
  
Then, use the token stored in the TOKEN variable and call the service, for example:
  
```shell
curl -X 'GET' \
  'http://localhost:8080/api/v1/yourrents/geodata/regions' \
  -H 'accept: */*' \
  -H "Authorization: Bearer $TOKEN"
```

#### Using Thunder Client

If you use [Thunder Client](https://www.thunderclient.com) for VS Code, you can import the `thunder_client/thunder-collection_YourRents API.json` file, for importing a collection of requests, with authentication and authorization already configured.

The configuration of the collection is based on some environment variables.

Load the environment variables from the `thunder_client/thunder-environment_YourRents development.json` file, and activate it, or attach it to the collection.

Then, you can run the requests and get the responses.

#### Using Swagger UI

You can also use the Swagger UI at <http://localhost:8080/swagger-ui.html> to call the services.

Authenticate using the token obtained with the `curl` command.

### Calling the service deployed with Docker Compose

You can deploy the service with Docker Compose using the `docker-compose-yrs-api.yml` file.

#### Using `curl`

Run the following command to get an access token:

```shell
TOKEN=$(curl -X POST \
  http://keycloak.local:18080/realms/your-rents/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d username=user \
  -d password=user \
  -d grant_type=password \
  -d client_id=your-rents-api \
  -d client_secret=ZEeQ3Zmhnm3NX6QPGlEpPOLB2OavM3GZ \
  | jq -r .access_token)
```

(remember to assign the `keycloak.local` domain name to the Keycloak server, as explained above)

Then, use the token stored in the TOKEN variable and call the service, for example:
  
```shell
curl -X 'GET' \
  'http://localhost:8080/api/v1/yourrents/geodata/regions' \
  -H 'accept: */*' \
  -H "Authorization: Bearer $TOKEN"
```

#### Using Thunder Client

If you use [Thunder Client](https://www.thunderclient.com) for VS Code, you can import the `thunder_client/thunder-collection_YourRents API.json` file, for importing a collection of requests, with authentication and authorization already configured.

The configuration of the collection is based on some environment variables.

Load the environment variables from the `thunder_client/thunder-environment_YourRents on Docker.json` file, and activate it, or attach it to the collection.

Then, you can run the requests and get the responses.

#### Using Swagger UI

You can also use the Swagger UI at <http://localhost:8080/swagger-ui.html> to call the services.

Authenticate using the token obtained with the `curl` command.

## CORS configuration

The service is configured to accept requests from the `http://localhost:4200` domain.

You can change this configuration by setting the `yrs-api.cors.allowed-origins` property or the `YRS-API_CORS_ALLOWED-ORIGINS` environment variable with a comma-separated list of allowed origins.

## See also

- [Development notes](DEVELOPMENT.md)
