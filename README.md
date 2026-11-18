# Carrito de Compras — Backend (Java + Spring Boot + MySQL)

API REST educativa para el proyecto de carrito de compras.

## Requisitos
- Java 25 (ya incluido Maven Wrapper `mvnw.cmd`)
- MySQL (Laragon en `localhost:3306`, usuario `root` sin contraseña por defecto)

## Cómo ejecutar
1. Inicia MySQL en Laragon.
2. (Opcional) Ejecuta `db/schema.sql` en HeidiSQL. Al arrancar, Spring crea las tablas y carga 5 productos si la tabla está vacía.
3. Levanta la API:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   Disponible en `http://localhost:8080`.

## Endpoints
- `GET /api/products` — lista productos activos
- `GET /api/products/{id}` — detalle de un producto
- `POST /api/orders` — crea un pedido dentro de una transacción (calcula precios desde la BD y descuenta stock)
- `GET /api/orders/{id}` — detalle de un pedido
