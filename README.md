# Demo — Spring Boot API

A Spring Boot REST API backed by PostgreSQL that now ships with a fully fledged authentication/authorization flow using Spring Security and JWT access/refresh tokens. The system also includes a dedicated user management microservice that exposes CRUD-style APIs for user profiles and consumes the JWT access tokens issued by the auth service.

## Authentication API quickstart

| Endpoint | Method | Description |
| --- | --- | --- |
| `/auth/register` | POST | Register a new user (`{ "username": "alice", "email": "alice@example.com", "password": "secret" }`) |
| `/auth/login` | POST | Exchange email/password for an access & refresh token pair |
| `/auth/refresh` | POST | Rotate a refresh token and receive a new pair |
| `/auth/logout` | POST | Revoke the provided refresh token |
| `/auth/me` | GET | Debug endpoint returning the authenticated principal & authorities |

## User management API

| Endpoint | Method | Description |
| --- | --- | --- |
| `/users/me` | GET | Fetch the profile for the authenticated user |
| `/users/me` | PUT | Create or update the profile for the authenticated user |
| `/users/me` | DELETE | Delete the authenticated user's profile |
| `/users` | GET | (Admin) List all user profiles |
| `/users/{id}` | GET | (Admin) Fetch a profile by ID |
| `/users/{id}` | DELETE | (Admin) Delete a profile by ID |

All user-service endpoints require a valid access token issued by the auth service. Tokens must be passed via the standard `Authorization: Bearer <token>` header so that Traefik can forward them to the downstream services without any custom headers.

## Running locally with Docker Compose & Traefik

The repository ships with a root-level `docker-compose.yml` that wires together:

* Traefik reverse proxy (port `8080`)
* The authentication microservice and its PostgreSQL database
* The user management microservice and its PostgreSQL database

To launch the full stack:

```bash
docker compose up --build
```

The auth API is available through Traefik at `http://localhost:8080/auth/...` while the user API is exposed at `http://localhost:8080/users/...`. By default both services use the same `JWT_SECRET` so a token minted by the auth service is accepted by the user service as long as it is supplied via the `Authorization` header.

Sample workflow (assumes the app runs on `http://localhost:8080`):
