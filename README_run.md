# Run Instructions

## Build
Each microservice is isolated. From the repository root, run the build for the desired service (or all of them):

```bash
# Build auth-service
(cd auth-service && ./mvnw clean package)

# Build user-service
(cd user-service && ./mvnw clean package)

# Build device-service
(cd device-service && ./mvnw clean package)
```

> `./mvnw` is a thin wrapper that delegates to the local Maven installation. Install Maven 3.9+ if not already available.

## Run All Services with Traefik Gateway

```bash
docker compose up -d --build
```

This command launches:

- Traefik gateway at http://localhost
- Auth service reachable at http://localhost/auth/
- User service reachable at http://localhost/users/
- Device service reachable at http://localhost/devices/

To stop everything:

```bash
docker compose down -v
```

## Run Individual Services
Each service directory ships with its own `docker-compose.yml` for isolated development:

```bash
# Example: run only the auth-service stack
(cd auth-service && docker compose up -d --build)
```

## Quick Test Flow

1. **Register an initial admin** (first user can request the ADMIN role)
   ```bash
   ADMIN=$(curl -s -X POST http://localhost/auth/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"AdminPass123!","role":"ADMIN"}')
   echo "$ADMIN" | jq
   ```

2. **Login as admin and capture the token**
   ```bash
   ADMIN_TOKEN=$(curl -s -X POST http://localhost/auth/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"AdminPass123!"}' | jq -r '.token')
   echo "Admin token: $ADMIN_TOKEN"
   ```

3. **Register a client account** (no header needed; defaults to CLIENT)
   ```bash
   CLIENT_PAYLOAD=$(curl -s -X POST http://localhost/auth/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"client1","password":"ClientPass123!"}')
   CLIENT_ID=$(echo "$CLIENT_PAYLOAD" | jq -r '.id')
   echo "Client id: $CLIENT_ID"
   ```

4. **Mirror the client in the user-service metadata (admin-only)**
   ```bash
   curl -X POST http://localhost/users/api/users \
     -H "Authorization: Bearer $ADMIN_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"id":"'"$CLIENT_ID"'","username":"client1","role":"CLIENT"}' | jq
   ```

5. **Create a device and assign it to the client**
   ```bash
   DEVICE_ID=$(curl -s -X POST http://localhost/devices/api/devices \
     -H "Authorization: Bearer $ADMIN_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"name":"Boiler","maxConsumption":150.5}' | jq -r '.id')

   curl -X POST http://localhost/devices/api/devices/$DEVICE_ID/assign/$CLIENT_ID \
     -H "Authorization: Bearer $ADMIN_TOKEN"
   ```

6. **Login as the client and view assigned devices**
   ```bash
   CLIENT_TOKEN=$(curl -s -X POST http://localhost/auth/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"client1","password":"ClientPass123!"}' | jq -r '.token')

   curl http://localhost/devices/api/my/devices \
     -H "Authorization: Bearer $CLIENT_TOKEN" | jq
   ```

> In production, store secrets such as `JWT_SECRET` and database passwords in a secure secret manager or `.env` file.
