# Gateway

Traefik is used as a lightweight reverse proxy. Each service registers HTTP routers via Docker labels:

- `/auth` → auth-service (port 8081)
- `/users` → user-service (port 8082)
- `/devices` → device-service (port 8083)

During development the Traefik dashboard is exposed at `http://localhost:8080` (insecure, do not enable in production).
