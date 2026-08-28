# 🏦 Demo Service: Demostración de Lógica de Negocio en la Capa de Servicio (api1)

Proyecto educativo desarrollado con **Spring Boot**, **Spring Data JPA** y **Postman** que demuestra de forma simple y didáctica cómo estructurar una aplicación por capas (**Model**, **Repository**, **Service**, **Controller**, **Config**) con **2 entidades relacionadas**, manteniendo el código directo, limpio y centrado primordialmente en la **Lógica de Negocio** en el servicio.

---

## 📋 Índice

1. [Arquitectura y Estructura del Proyecto](#-arquitectura-y-estructura-del-proyecto)
2. [Entidades y Relación](#-entidades-y-relación)
3. [Reglas de Negocio Implementadas](#-reglas-de-negocio-implementadas)
4. [Datos Iniciales de Prueba](#-datos-iniciales-de-prueba)
5. [Requisitos y Configuración de Base de Datos](#-requisitos-y-configuración-de-base-de-datos)
6. [Cómo Ejecutar la Aplicación](#-cómo-ejecutar-la-aplicación)
7. [Pruebas con Postman](#-pruebas-con-postman)
   - [Importación de la Colección](#importación-de-la-colección-recomendado---2-clics)
   - [🟢 Casos de Éxito en Postman (Reglas Cumplidas)](#-casos-de-éxito-en-postman-reglas-cumplidas)
   - [🔴 Casos de Error en Postman (Reglas Incumplidas)](#-casos-de-error-en-postman-reglas-incumplidas)
   - [📑 Modelos JSON para Postman (Request / Response)](#-modelos-json-para-postman-request--response)

---

## 🏛 Arquitectura y Estructura del Proyecto

El código está organizado bajo el paquete principal `com.example.demo_service`:

```text
com.example.demo_service/
├── config/
│   ├── DataInitializer.java        # Semilla de datos (valida si la BD ya tiene cuentas)
│   └── OpenApiConfig.java           # Configuración OpenAPI
├── controller/
│   ├── CuentaController.java       # Endpoints REST para consulta y creación de cuentas
│   └── MovimientoController.java   # Endpoints REST para transacciones bancarias
├── exception/
│   └── ReglaNegocioException.java  # Excepción con @ResponseStatus(BAD_REQUEST) para fallos de reglas
├── model/
│   ├── Cuenta.java                 # Entidad 1: Cuenta bancaria
│   └── Movimiento.java             # Entidad 2: Movimiento o transacción financiera
├── repository/
│   ├── CuentaRepository.java       # Interfaz Spring Data JPA para persistencia de Cuenta
│   └── MovimientoRepository.java   # Interfaz Spring Data JPA para persistencia de Movimiento
└── service/
    ├── CuentaService.java          # Servicios auxiliares para cuentas
    └── MovimientoService.java      # NÚCLEO: Donde reside y se valida la LÓGICA DE NEGOCIO
```

### Flujo de Ejecución por Capas:
```text
  [ Postman ]
      │
      ▼
  [ Controller Layer ]         <-- Recibe la petición y delega al servicio
      │
      ▼
  [ Service Layer ]            <-- Valida y ejecuta las REGLAS DE NEGOCIO
      │
      ▼
  [ Repository Layer ]         <-- Persiste mediante Spring Data JPA
      │
      ▼
  [ Base de Datos PostgreSQL ] <-- Persistencia relacional
```

---

## 📦 Entidades y Relación

### 1. `Cuenta`
Representa una cuenta bancaria con saldo y estado de activación:
- `id` (Long): Clave primaria autoincremental.
- `numeroCuenta` (String): Código de cuenta (ej. `"CTA-1001"`).
- `titular` (String): Nombre del cliente.
- `saldo` (Double): Saldo disponible.
- `activa` (Boolean): Habilita o bloquea operaciones financieras.

### 2. `Movimiento`
Representa una transacción realizada sobre una cuenta:
- `id` (Long): Clave primaria.
- `cuenta` (`@ManyToOne` con `Cuenta`): Relación directa a la cuenta asociada.
- `tipo` (String): `"DEPOSITO"` o `"RETIRO"`.
- `monto` (Double): Importe de la transacción.
- `saldoResultante` (Double): Saldo de la cuenta tras ejecutarse el movimiento (calculado en el servicio).
- `fecha` (LocalDateTime): Momento exacto del procesamiento.

---

## 🧠 Reglas de Negocio Implementadas

Toda operación que ingresa al endpoint `POST /api/movimientos` es interceptada y validada en el núcleo del servicio (**[`MovimientoService.java`](file:///c:/Users/Jhon/Desktop/demo-service/src/main/java/com/example/demo_service/service/MovimientoService.java)**):

| # | Regla / Validación | Condición de Cumplimiento (Éxito) | Mensaje al Incumplirse (`HTTP 400 Bad Request`) |
|:--:|---|---|---|
| **1** | **Monto Válido** | `monto != null && monto > 0` | `"El monto de la transacción debe ser mayor a 0."` |
| **2** | **Tipo de Movimiento** | `"DEPOSITO"` o `"RETIRO"` | `"El tipo de movimiento debe ser 'DEPOSITO' o 'RETIRO'."` |
| **3** | **ID de Cuenta Requerido** | `cuenta != null && cuenta.getId() != null` | `"Debe especificar el ID de la cuenta bancaria."` |
| **4** | **Existencia de Cuenta** | Cuenta existente en BD | `"Cuenta no encontrada con ID: {id}"` |
| **5** | **Cuenta Activa** | `cuenta.activa == true` | `"Regla de negocio INCUMPLIDA [Cuenta Inactiva]: La cuenta {num} ({titular}) se encuentra bloqueada o inactiva y no permite transacciones."` |
| **6** | **Límite por Retiro** | `monto <= 1000.0` (solo retiros) | `"Regla de negocio INCUMPLIDA [Límite Excedido]: El retiro solicitado de ${monto} supera el límite máximo permitido de $1000.0 por operación."` |
| **7** | **Saldo Suficiente** | `cuenta.saldo >= monto` (solo retiros) | `"Regla de negocio INCUMPLIDA [Saldo Insuficiente]: La cuenta {num} cuenta con un saldo de ${saldo}, insuficiente para retirar ${monto}."` |
| **8** | **Cálculo y Auditoría** | Depósito: `saldo + monto`<br>Retiro: `saldo - monto` | *Actualiza el saldo en `Cuenta` y guarda en `Movimiento` el `saldoResultante` y la `fecha` actual.* |

### Manejo Simple de Excepciones (Sin Clases Globales Complejas)
- **[`ReglaNegocioException.java`](file:///c:/Users/Jhon/Desktop/demo-service/src/main/java/com/example/demo_service/exception/ReglaNegocioException.java)**: Hereda de `RuntimeException` anotada con `@ResponseStatus(HttpStatus.BAD_REQUEST)`.
- **Manejador en Controlador**: Mediante `@ExceptionHandler` directo en los controladores, devuelve una respuesta JSON limpia (`status: 400`, `error: "Bad Request"`, `message: "..."`, `path: "..."`) sin rastros de stack trace (`trace`).

---

## 🗄 Datos Iniciales de Prueba

El componente `DataInitializer` verifica primero si la tabla `cuentas` está vacía (`count() == 0`) antes de sembrar los datos. Esto evita duplicados al trabajar con bases de datos persistentes:

| ID | Número Cuenta | Titular | Saldo Inicial | Estado | Propósito de Prueba en Postman |
|:--:|:-------------:|:-------:|:-------------:|:------:|:-------------------------------|
| **1** | `CTA-1001` | Carlos Gómez | **$500.0** | **Activa (`true`)** | Ideal para probar depósitos y retiros exitosos, o saldo insuficiente (> $500). |
| **2** | `CTA-1002` | Ana Martínez | **$300.0** | **Inactiva (`false`)** | Ideal para probar el rechazo por cuenta bloqueada. |

---

## ⚙️ Requisitos y Configuración de Base de Datos

### Prerrequisitos
- **Java 17** o superior instalado.
- Servidor o base de datos PostgreSQL (ej. Neon DB, PostgreSQL local o Docker).

### Variables de Entorno (`application.properties`)
La aplicación obtiene sus credenciales mediante variables de entorno:

```properties
spring.application.name=api1

# Conexión a la Base de Datos mediante variables de entorno
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Puerto del servidor (Por defecto 8080)
server.port=${PORT:8080}

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Puedes definir las variables en un archivo `.env` en la raíz del proyecto:
```env
DB_URL=jdbc:postgresql://<HOST>/<DATABASE>?sslmode=require
DB_USERNAME=<USUARIO>
DB_PASSWORD=<PASSWORD>
PORT=8080
```

---

## 🚀 Cómo Ejecutar la Aplicación

En Windows (PowerShell):

```powershell
# 1. Definir variables de entorno en la sesión
$env:DB_URL="jdbc:postgresql://<HOST>/<DATABASE>?sslmode=require"
$env:DB_USERNAME="<USUARIO>"
$env:DB_PASSWORD="<PASSWORD>"
$env:PORT="8080"

# 2. Iniciar la aplicación
./mvnw spring-boot:run
```

Una vez iniciada, la API estará escuchando en: **`http://localhost:8080`**.

---

## 📮 Pruebas con Postman

Todas las pruebas del proyecto se realizan a través de **Postman**. Para ello se incluye la colección completa pre-configurada en el archivo **[`postman_collection.json`](file:///c:/Users/Jhon/Desktop/demo-service/postman_collection.json)**.

### Importación de la Colección (Recomendado - 2 Clics)

1. Abre **Postman**.
2. Haz clic en el botón **Import** (esquina superior izquierda).
3. Selecciona o arrastra el archivo `postman_collection.json` ubicado en la raíz del proyecto (`c:\Users\Jhon\Desktop\demo-service\postman_collection.json`).
4. Haz clic en **Import**.
5. Tendrás lista la colección **`Demo Service - Lógica de Negocio Bancaria (2 Entidades)`** organizada en carpetas con cada petición lista para enviar:

```text
📁 Demo Service - Lógica de Negocio Bancaria (2 Entidades)
├── 📁 1. Cuentas
│   ├── GET Listar Cuentas
│   ├── GET Obtener Cuenta por ID (1)
│   └── POST Crear Nueva Cuenta
├── 📁 2. Reglas Cumplidas (Éxito)
│   ├── POST CASO 1: Depósito Válido (+ $200.0)
│   └── POST CASO 2: Retiro Válido (- $150.0)
├── 📁 3. Reglas Incumplidas (Errores)
│   ├── POST CASO 3: Falla por Saldo Insuficiente (Retiro $950)
│   ├── POST CASO 4: Falla por Límite Excedido (Retiro $2500)
│   ├── POST CASO 5: Falla por Cuenta Inactiva (Cuenta ID 2)
│   └── POST CASO 6: Falla por Monto Inválido (-$50.0)
└── 📁 4. Historial de Movimientos
    ├── GET Listar Todos los Movimientos
    └── GET Listar Movimientos por Cuenta (ID 1)
```

---

### 🟢 Casos de Éxito en Postman (Reglas Cumplidas)

#### CASO 1: Depósito Válido
* **Petición en Postman:** Seleccionar `CASO 1: Depósito Válido (+ $200.0)`
* **Método y URL:** `POST http://localhost:8080/api/movimientos`
* **Body (raw JSON):**
```json
{
  "cuenta": { "id": 1 },
  "tipo": "DEPOSITO",
  "monto": 200.0
}
```
* **Respuesta Esperada (`HTTP 201 Created`):**
```json
{
  "id": 1,
  "cuenta": {
    "id": 1,
    "numeroCuenta": "CTA-1001",
    "titular": "Carlos Gómez",
    "saldo": 700.0,
    "activa": true
  },
  "tipo": "DEPOSITO",
  "monto": 200.0,
  "saldoResultante": 700.0,
  "fecha": "2026-09-09T14:00:00"
}
```
> ✅ **Efecto de Negocio:** El saldo de la cuenta aumentó de `$500.0` a `$700.0`.

---

#### CASO 2: Retiro Válido
* **Petición en Postman:** Seleccionar `CASO 2: Retiro Válido (- $150.0)`
* **Método y URL:** `POST http://localhost:8080/api/movimientos`
* **Body (raw JSON):**
```json
{
  "cuenta": { "id": 1 },
  "tipo": "RETIRO",
  "monto": 150.0
}
```
* **Respuesta Esperada (`HTTP 201 Created`):**
```json
{
  "id": 2,
  "cuenta": {
    "id": 1,
    "numeroCuenta": "CTA-1001",
    "titular": "Carlos Gómez",
    "saldo": 550.0,
    "activa": true
  },
  "tipo": "RETIRO",
  "monto": 150.0,
  "saldoResultante": 550.0,
  "fecha": "2026-09-09T14:00:05"
}
```
> ✅ **Efecto de Negocio:** El saldo se debitó correctamente de `$700.0` a `$550.0`.

---

### 🔴 Casos de Error en Postman (Reglas Incumplidas)

Todas las excepciones de reglas de negocio son gestionadas y retornan un código **`HTTP 400 Bad Request`** con formato JSON limpio y sin traces:

#### CASO 3: Falla por Saldo Insuficiente
* **Petición en Postman:** Seleccionar `CASO 3: Falla por Saldo Insuficiente (Retiro $950)`
* **Método y URL:** `POST http://localhost:8080/api/movimientos`
* **Body (raw JSON):**
```json
{
  "cuenta": { "id": 1 },
  "tipo": "RETIRO",
  "monto": 950.0
}
```
* **Respuesta Esperada (`HTTP 400 Bad Request`):**
```json
{
  "timestamp": "2026-09-09T16:04:45.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Regla de negocio INCUMPLIDA [Saldo Insuficiente]: La cuenta CTA-1001 cuenta con un saldo de $550.0, insuficiente para retirar $950.0.",
  "path": "/api/movimientos"
}
```
> ❌ **Efecto de Negocio:** Transacción abortada. El saldo de la cuenta permanece intacto en `$550.0`.

---

#### CASO 4: Falla por Límite Máximo Superado (> $1,000.00)
* **Petición en Postman:** Seleccionar `CASO 4: Falla por Límite Excedido (Retiro $2500)`
* **Método y URL:** `POST http://localhost:8080/api/movimientos`
* **Body (raw JSON):**
```json
{
  "cuenta": { "id": 1 },
  "tipo": "RETIRO",
  "monto": 2500.0
}
```
* **Respuesta Esperada (`HTTP 400 Bad Request`):**
```json
{
  "timestamp": "2026-09-09T16:04:45.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Regla de negocio INCUMPLIDA [Límite Excedido]: El retiro solicitado de $2500.0 supera el límite máximo permitido de $1000.0 por operación.",
  "path": "/api/movimientos"
}
```

---

#### CASO 5: Falla por Cuenta Inactiva o Bloqueada
* **Petición en Postman:** Seleccionar `CASO 5: Falla por Cuenta Inactiva (Cuenta ID 2)`
* **Método y URL:** `POST http://localhost:8080/api/movimientos`
* **Body (raw JSON):**
```json
{
  "cuenta": { "id": 2 },
  "tipo": "DEPOSITO",
  "monto": 100.0
}
```
* **Respuesta Esperada (`HTTP 400 Bad Request`):**
```json
{
  "timestamp": "2026-09-09T16:04:45.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Regla de negocio INCUMPLIDA [Cuenta Inactiva]: La cuenta CTA-1002 (Ana Martínez) se encuentra bloqueada o inactiva y no permite transacciones.",
  "path": "/api/movimientos"
}
```

---

#### CASO 6: Falla por Monto Inválido (Cero o Negativo)
* **Petición en Postman:** Seleccionar `CASO 6: Falla por Monto Inválido (-$50.0)`
* **Método y URL:** `POST http://localhost:8080/api/movimientos`
* **Body (raw JSON):**
```json
{
  "cuenta": { "id": 1 },
  "tipo": "DEPOSITO",
  "monto": -50.0
}
```
* **Respuesta Esperada (`HTTP 400 Bad Request`):**
```json
{
  "timestamp": "2026-09-09T16:04:45.123",
  "status": 400,
  "error": "Bad Request",
  "message": "El monto de la transacción debe ser mayor a 0.",
  "path": "/api/movimientos"
}
```

---

### 📑 Modelos JSON para Postman (Request / Response)

Estructura de datos para el envío y recepción de información en las solicitudes:

#### 1. Modelo `Cuenta`

##### A) Petición para Crear Cuenta (`POST /api/cuentas`)
* **Headers:** `Content-Type: application/json`
* **Body (raw JSON):**
```json
{
  "numeroCuenta": "CTA-2001",
  "titular": "Laura Rivas",
  "saldo": 1000.0,
  "activa": true
}
```

##### B) Respuesta de Cuenta (`HTTP 201 Created` / `HTTP 200 OK`)
```json
{
  "id": 3,
  "numeroCuenta": "CTA-2001",
  "titular": "Laura Rivas",
  "saldo": 1000.0,
  "activa": true
}
```

##### Atributos del Modelo `Cuenta`:
| Campo | Tipo | Requerido | Descripción |
|---|---|:---:|---|
| `id` | `Long` | No (Auto) | Clave primaria generada por la base de datos. |
| `numeroCuenta` | `String` | Sí | Código único de la cuenta (ej. `"CTA-1001"`). |
| `titular` | `String` | Sí | Nombre completo del titular. |
| `saldo` | `Double` | Opcional | Saldo inicial (por defecto `0.0`). |
| `activa` | `Boolean` | Opcional | Estado que habilita operaciones (`true` / `false`). |

---

#### 2. Modelo `Movimiento`

##### A) Petición para Registrar Movimiento (`POST /api/movimientos`)
* **Headers:** `Content-Type: application/json`

**Para Depósito:**
```json
{
  "cuenta": {
    "id": 1
  },
  "tipo": "DEPOSITO",
  "monto": 200.0
}
```

**Para Retiro:**
```json
{
  "cuenta": {
    "id": 1
  },
  "tipo": "RETIRO",
  "monto": 150.0
}
```

##### B) Respuesta Exitosa (`HTTP 201 Created`)
```json
{
  "id": 1,
  "cuenta": {
    "id": 1,
    "numeroCuenta": "CTA-1001",
    "titular": "Carlos Gómez",
    "saldo": 700.0,
    "activa": true
  },
  "tipo": "DEPOSITO",
  "monto": 200.0,
  "saldoResultante": 700.0,
  "fecha": "2026-09-09T14:00:00"
}
```

##### C) Respuesta de Error (Regla Incumplida - HTTP 400 Bad Request)
```json
{
  "timestamp": "2026-09-09T20:53:33.322Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Regla de negocio INCUMPLIDA [Saldo Insuficiente]: La cuenta CTA-1001 cuenta con un saldo de $550.0, insuficiente para retirar $950.0.",
  "path": "/api/movimientos"
}
```

##### Atributos del Modelo `Movimiento`:
| Campo | Tipo | Enviado en Postman | Calculado por el Servicio | Descripción |
|---|---|:---:|:---:|---|
| `id` | `Long` | ❌ No | ✅ Sí | ID autoincremental de la transacción. |
| `cuenta` | `Object` | ✅ Sí (`{"id": X}`) | ✅ Sí | Objeto con la referencia al ID de la cuenta bancaria. |
| `tipo` | `String` | ✅ Sí | ❌ No | Tipo de operación: `"DEPOSITO"` o `"RETIRO"`. |
| `monto` | `Double` | ✅ Sí | ❌ No | Valor numérico mayor a 0 a debitar o acreditar. |
| `saldoResultante`| `Double` | ❌ No | ✅ Sí | Saldo resultante final tras aplicar la lógica de negocio. |
| `fecha` | `LocalDateTime` | ❌ No | ✅ Sí | Marca de tiempo exacta del registro de la transacción. |
