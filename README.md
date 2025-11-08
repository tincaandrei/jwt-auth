# Energy Management Platform

A multi-service Spring Boot system that delivers authentication, user management, and device management for an energy monitoring
platform. Each microservice is packaged for standalone development and runs together behind a Traefik reverse proxy and a shared
PostgreSQL instance when using Docker Compose.

## Architecture

| Service | Description | Internal Port | Traefik route |
| --- | --- | --- | --- |
| auth-service | Issues JWT access/refresh tokens, manages identities and roles. | 8080 | `http://localhost:8080/api/auth` |
| user-service | CRUD operations for user profiles that are linked to auth-service accounts. | 8081 | `http://localhost:8080/api/users` |
| device-service | CRUD and assignment flow for IoT devices mapped to platform users. | 8082 | `http://localhost:8080/api/devices` |
| db | PostgreSQL 16 with dedicated databases per service, initialised via `docker/db/init.sql`. | 5432 | not exposed |
| traefik | Reverse proxy + API gateway that forwards `/api/*` traffic to the corresponding service. | 80 (exposed on host `8080`) | entrypoint |

All services expose Spring Boot Actuator health/info endpoints and OpenAPI documentation (`/swagger-ui.html`). JWT tokens minted
by auth-service are validated downstream by user-service and device-service.

Default roles:

* `ROLE_ADMIN` – full access to all management APIs.
* `ROLE_CLIENT` – owns personal profile and assigned devices.

## Local development without Docker

1. Provision PostgreSQL (example using Docker):
   ```bash
   docker run --name energy-db -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16-alpine
   docker exec -i energy-db psql -U postgres <<'SQL'
   CREATE USER auth_user WITH PASSWORD 'auth_pwd';
   CREATE DATABASE auth_db OWNER auth_user;
   CREATE USER user_user WITH PASSWORD 'user_pwd';
   CREATE DATABASE user_db OWNER user_user;
   CREATE USER device_user WITH PASSWORD 'device_pwd';
   CREATE DATABASE devices_db OWNER device_user;
   SQL
   ```
2. Copy `.env.example` to `.env` and adjust secrets if needed.
3. Start each service from its folder (example for device-service):
   ```bash
   cd device
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   Repeat for `auth` and `user` directories.
4. Access APIs directly on their local ports (8080/8081/8082) or front them with your own reverse proxy.

## Docker Compose workflow

The root `docker-compose.yml` orchestrates Traefik, PostgreSQL, and the three services. All images are built with multi-stage
Dockerfiles that use the Maven wrapper for builds and run on Temurin JRE images with health checks enabled.

```bash
docker compose up -d --build
```

Key behaviour:

* Traefik listens on `http://localhost:8080` and routes requests by path prefix: `/api/auth`, `/api/users`, and `/api/devices`.
* PostgreSQL persists data in the `db_data` volume and initialises databases/users for every service on first start.
* Containers wait for PostgreSQL health before starting (via `depends_on` with health conditions).
* Health checks poll `/actuator/health` to ensure services are ready.

To tear down the environment:

```bash
docker compose down -v
```

## API highlights

Each service serves an OpenAPI definition at `/v3/api-docs` and an interactive UI at `/swagger-ui.html` (also behind Traefik).

### Auth service

* `POST /api/auth/register` – create user credentials (admin seeding happens on bootstrap).
* `POST /api/auth/login` – obtain access + refresh tokens.
* `POST /api/auth/refresh` – rotate refresh token.
* `POST /api/auth/logout` – revoke refresh token.
* `GET /api/auth/me` – inspect current principal and granted authorities.

### User service

* `GET /api/users/me` – fetch the authenticated user profile.
* `PUT /api/users/me` – create/update the caller's profile.
* `DELETE /api/users/me` – remove the caller's profile.
* `GET /api/users` – ADMIN only, list every profile.
* `GET /api/users/{id}` – ADMIN only, fetch by ID.
* `DELETE /api/users/{id}` – ADMIN only, delete by ID.

### Device service

* `POST /api/devices` – ADMIN only, create a device.
* `GET /api/devices` – ADMIN lists all devices; CLIENT users list their own. Use `?owner=me` to filter explicitly.
* `GET /api/devices/{id}` – ADMIN or assigned owner can view details.
* `PUT /api/devices/{id}` – ADMIN only, update name/description/max consumption.
* `DELETE /api/devices/{id}` – ADMIN only, delete device.
* `POST /api/devices/{id}/assign/{userId}` – ADMIN only, assign to a user UUID.
* `POST /api/devices/{id}/unassign` – ADMIN only, remove assignment.

## Configuration & secrets

* All services read database and JWT settings from environment variables. Defaults are set for local development and Docker
  through the Spring `application-*.yml` profiles.
* Copy `.env.example` to `.env` for local overrides (Compose automatically loads it).
* Toggle security in device-service (useful for tests) via `APP_SECURITY_ENABLED=false`.

## Testing

Each service ships with Maven wrapper scripts. Run tests from the service directory:

```bash
./mvnw test
```

Device-service includes unit tests for the service and controller layers. Auth and user services retain their existing test suites.

## Troubleshooting

* **`./mvnw` not found inside Docker builds** – ensure `.dockerignore` does *not* exclude `.mvn`, `mvnw`, `pom.xml`, or `src/` (this
  repository already applies the safe pattern).
* **Database connection errors** – confirm the PostgreSQL container is healthy (`docker ps --format '{{.Names}}\t{{.Status}}'`).
* **JWT mismatches** – Traefik forwards the `Authorization` header unchanged; verify all services use the same `JWT_SECRET`.
* **Swagger/UI unreachable** – expose `/swagger-ui.html` via Traefik at `/api/<service>/swagger-ui.html`.
