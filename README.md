# 🏥 Hospital Operations Service

A robust RESTful backend service for handling **critical hospital operations** (e.g. discharging a patient) with strong guarantees around **idempotency**, **exactly-once processing**, and **failure resilience**.

This service is designed to be safely used by clinicians through a mobile application where **network instability, retries, and server crashes are expected** and must not lead to lost or duplicated operations.

---

## ✨ Key Features

- **Idempotent REST APIs** using `Idempotency-Key`
- **Exactly-once processing** backed by persistent storage
- **Undo / compensation support** for all operations
- **Explicit operation lifecycle** (`PENDING`, `COMPLETED`, etc.)
- **Database-level constraints** to prevent invalid states
- **Clean, consistent error handling**
- Minimal, extensible architecture built on Spring Boot

---

## 🧠 Core Design Principles

### 1. Idempotency

All write operations require an `Idempotency-Key` header.

- The key is persisted and enforced with a **unique database constraint**
- Retrying the same request:
  - Does **not** create duplicate operations
  - Returns the already-existing operation if it was previously processed
- This allows safe retries from clients after:
  - Network timeouts
  - Connection drops
  - App restarts

---

### 2. Guaranteed Delivery & Exactly-Once Processing

To ensure operations are never lost:

- Requests are **persisted before processing**
- Each operation has an explicit **status lifecycle**
- The database is the source of truth
- Unique constraints prevent race conditions and duplication

Even if the server crashes mid-request, the operation remains durable and recoverable.

---

### 3. Operation Lifecycle

Each operation progresses through a defined lifecycle:

- RECEIVED, COMPLETED
- RECEIVED_UNDO, UNDONE

This makes system behavior explicit, auditable, and safe to retry.

---

### 4. Business Constraints (Enforced at DB Level)

The system enforces critical invariants using database constraints:

- **Unique `idempotency_key`**
- **Composite unique constraint on `(patient_id, status)`**

This guarantees:
- No duplicate operations per idempotency key
- Only one operation per patient per status
- Protection against concurrent or conflicting requests

---

### 5. Undo / Compensation

Operations are never deleted.

Undoing an operation is handled via a **compensating action** that:

- Transitions the operation to an `UNDONE` state
- Preserves history and auditability
- Allows safe correction of mistakes (e.g. undo discharge)

---

## 🧩 Architecture Overview



- **Controllers** handle HTTP & validation
- **Services** enforce idempotency and lifecycle rules
- **Repositories** provide persistence
- **Global exception handling** converts DB errors into clean API responses

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- H2 (development) 
- Maven

---

## 🚀 Running the Application

### Prerequisites

- Java 17+
- Maven

### Run locally

```bash
git clone https://github.com/georstar/hospital.git
cd hospital
./mvnw spring-boot:run
```

## Local Build with Docker 🐳

Follow these steps to build and run the project locally using Docker:

1. **Navigate to the Dockerfile directory**  
   Make sure you are in the same folder as the `Dockerfile`.

2. **Build and run the Docker image**  
   ```bash
   docker build -t hospital .
   docker run -d -p 4000:4000 hospital
   ```
   
### 🔎 Swagger UI

Once the application is running, Swagger UI is available at:

http://localhost:4000/swagger-ui/index.html

---

## 🧪 Testing the API

The service can be tested both **interactively via Swagger UI** and **manually using cURL**.

In the folder /api-requests, can be found .http requests for all endpoints.

Also, try through curl:
   ```bash
   curl -X POST http://localhost:4000/operations/123/do \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: 123e4567-e89b-12d3-a456-426614174000" \
  -d '{
    "operationType": "DISCHARGE_PATIENT"
  }'
   ```

   ```bash
   curl -X POST http://localhost:4000/operations/123/undo \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: 123e4567-e89b-12d3-a456-426614174001" \
  -d '{
    "operationType": "DISCHARGE_PATIENT"
  }'
   ```


---




