# Auth Service JWT Flow

This service issues JSON Web Tokens (JWT) that authenticate requests travelling through Traefik to the downstream services.

## Flow Overview
1. **Login** – `POST /auth/api/auth/login` receives credentials and, if valid, signs a JWT with `jwt.secret`. The subject stores the user UUID and the payload contains `username` and `role` claims.
2. **Gateway forwarding** – Traefik forwards requests sent to `http://localhost/auth/**`, `http://localhost/users/**`, and `http://localhost/devices/**` to their respective backends while preserving the `Authorization: Bearer <token>` header.
3. **Downstream validation** – The `user-service` and `device-service` reuse the same secret to validate the signature, extract the subject (user id), username, and role, and authorize requests.
4. **Authorization** – Services apply role checks (ADMIN or CLIENT) to guard CRUD endpoints or personalised queries such as `/api/my/devices`.

## Example (using Postman or curl)

```bash
# Register an initial admin (no token required, but the role defaults to CLIENT unless an ADMIN token is present)
curl -X POST http://localhost/auth/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"AdminPass123!","role":"ADMIN"}'

# Login and capture the token
TOKEN=$(curl -s -X POST http://localhost/auth/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"AdminPass123!"}' | jq -r '.token')

echo "Token: $TOKEN"

# Use the token when calling another service through Traefik
curl http://localhost/users/api/users \
  -H "Authorization: Bearer $TOKEN"

# Client specific endpoint (requires a CLIENT token)
curl http://localhost/devices/api/my/devices \
  -H "Authorization: Bearer $TOKEN"
```

Downstream services never issue new tokens—they strictly validate signatures and claims received from the auth-service.
