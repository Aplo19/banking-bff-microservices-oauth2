# Banco Platform - BFF + Microservicios

Arquitectura backend orientada a integracion de servicios financieros: un `BFF` consume dos microservicios (`clientes` y `productos`) protegidos con OAuth2/JWT, aplica encriptacion de `codigoUnico` y propaga `X-Trace-Id` para trazabilidad.

## Objetivo del proyecto
- Exponer un endpoint de integracion en el BFF que combine datos de cliente + productos.
- Asegurar el acceso con OAuth2 (Authorization Server + Resource Server).
- Validar que el `codigoUnico` llegue encriptado.
- Demostrar observabilidad basica con logs y `traceId`.

## Arquitectura
- `auth-server`: Authorization Server OAuth2.
- `bff`: Backend for Frontend, orquesta llamadas a `ms-cliente` y `ms-producto`.
- `ms-cliente`: servicio de datos de cliente.
- `ms-producto`: servicio de datos de productos financieros.
- `starter-banco`: starter reutilizable con auto-configuracion.
- `base de datos/financiera_db.sql`: script base de BD.

## Stack tecnico
- Java 17
- Spring Boot 3.5.x
- Spring Security + OAuth2 Authorization Server + Resource Server
- Spring WebFlux (BFF + microservicios)
- Spring Data JPA
- MapStruct + Lombok
- JUnit 5 + Mockito
- Docker / Docker Compose

## Prerrequisitos
- Java 17
- Maven 3.9+
- Docker + Docker Compose (opcion recomendada)
- MySQL (si ejecutas sin Docker para BD)

## Estructura del repo
```text
auth-server/
bff/
ms-cliente/
ms-producto/
starter-banco/
base de datos/
docker-compose.yml
```

## Quick Start (Docker) - recomendado
1. Clona el proyecto y entra a la carpeta raiz.
2. Levanta todo:
```bash
docker-compose up --build -d
```
3. Verifica servicios:
- `http://localhost:9000` (`auth-server`)
- `http://localhost:8086` (`bff`)
- `http://localhost:8085/api` (`ms-cliente`)
- `http://localhost:8084/api` (`ms-producto`)

Para detener:
```bash
docker-compose down
```

## Ejecucion local (sin Docker)
1. Crear base `financiera_db` en MySQL.
2. Instalar starter local:
```bash
cd starter-banco
mvn clean install
```
3. Ejecutar en este orden (terminales separadas):
```bash
cd auth-server && mvn spring-boot:run
cd ms-cliente && mvn spring-boot:run
cd ms-producto && mvn spring-boot:run
cd bff && mvn spring-boot:run
```

## Flujo funcional de prueba (end-to-end)
1. Obtener token OAuth2:
```bash
curl -u bff-service:bff123 \
  -d "grant_type=client_credentials&scope=internal" \
  http://localhost:9000/oauth2/token
```
2. Encriptar `codigoUnico` desde el BFF (solo apoyo en desarrollo):
```bash
curl -X POST "http://localhost:8086/v1/integracion/encriptar?codigoUnico=CLI001"
```
3. Consumir endpoint de integracion:
```bash
curl -X POST http://localhost:8086/v1/integracion/cliente-productos \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -H "X-Trace-Id: TRACE-TEST-001" \
  -d "{\"codigoUnico\":\"<CODIGO_ENCRIPTADO>\",\"traceId\":\"TRACE-TEST-001\"}"
```

## Endpoints principales
- BFF
- `POST /v1/integracion/cliente-productos`
- `POST /v1/integracion/encriptar` (solo desarrollo)
- `POST /v1/integracion/desencriptar` (solo desarrollo)
- Clientes
- `POST /api/v1/clientes/buscar`
- Productos
- `POST /api/v1/productos/buscar`

## OpenAPI / Swagger
- BFF: `http://localhost:8086/swagger-ui.html`
- ms-cliente: `http://localhost:8085/api/swagger-ui.html`
- ms-producto: `http://localhost:8084/api/swagger-ui.html`

## Seguridad y trazabilidad
- OAuth2 con `auth-server` como emisor de tokens.
- BFF y microservicios validan JWT como Resource Server.
- El header `X-Trace-Id` se propaga entre servicios para seguimiento de requests.
- Validacion de `codigoUnico` encriptado en el BFF.

## Configuracion relevante
- Docker usa variables de entorno en `docker-compose.yml`.
- Configuracion local en:
- `auth-server/src/main/resources/application.properties`
- `bff/src/main/resources/application.properties`
- `ms-cliente/src/main/resources/application.properties`
- `ms-producto/src/main/resources/application.properties`

## Testing
Ejecutar tests por modulo:
```bash
cd auth-server && mvn test
cd bff && mvn test
cd ms-cliente && mvn test
cd ms-producto && mvn test
```

## Notas para portafolio
- Este proyecto esta disenado como demostracion tecnica de arquitectura.
- Los endpoints `encriptar/desencriptar` del BFF se consideran de apoyo para pruebas.
- Para un entorno real: usar secretos por variables de entorno/secret manager y endurecer politicas de seguridad.

