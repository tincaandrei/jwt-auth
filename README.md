# Demo — Spring Boot API

A Spring Boot REST API backed by PostgreSQL that now ships with a fully fledged authentication/authorization flow using Spring Security and JWT access/refresh tokens.

## Authentication API quickstart

| Endpoint | Method | Description |
| --- | --- | --- |
| `/auth/register` | POST | Register a new user (`{ "username": "alice", "email": "alice@example.com", "password": "secret" }`) |
| `/auth/login` | POST | Exchange email/password for an access & refresh token pair |
| `/auth/refresh` | POST | Rotate a refresh token and receive a new pair |
| `/auth/logout` | POST | Revoke the provided refresh token |
| `/auth/me` | GET | Debug endpoint returning the authenticated principal & authorities |

Sample workflow (assumes the app runs on `http://localhost:8080`):
