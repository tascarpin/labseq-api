# Labseq API

- [🇧🇷 Documentação em Português](README_PT.md)

## 📘 Labseq API - Quarkus Project

This is a REST API built with **Quarkus 3.19.4** and **Java 21**, following **Clean Architecture** principles. It efficiently computes values from the Labseq sequence, even for very large numbers, using adaptive and optimized strategies powered by **BigInteger**, **threading**, and **in-memory memoization**.

---

### 🚀 Features

- RESTful API with Quarkus
- High-performance calculation using `BigInteger`
- Dynamic selection of optimal strategy based on input
- Modular design using Strategy Pattern and CDI
- In-memory caching with `ConcurrentHashMap`
- Clean, maintainable architecture
- Docker-ready for deployment

---

### 🧪 Tech Stack

- Java 21
- Quarkus 3.19.4
- Jakarta REST (JAX-RS)
- CDI (`jakarta.inject`)
- MicroProfile Config
- BigInteger
- Docker

---

# 📦 Project Structure

## 🧱 Architecture – Clean Architecture (REST-Oriented with Quarkus)

This project applies **Clean Architecture principles** tailored for a REST API using **Quarkus**. The goal is to maintain separation of concerns, loose coupling, and scalability — even in a seemingly simple service like Labseq sequence calculation.

---

### ✅ Why Clean Architecture?

Instead of a traditional MVC (Model-View-Controller) structure, this project uses Clean Architecture to ensure:

| Benefit                           | Description                                                                 |
|-----------------------------------|-----------------------------------------------------------------------------|
| 🧼 Separation of concerns          | Clear separation between business logic, orchestration, and interfaces     |
| 🧪 High testability                | Each layer is easily mockable and independently testable                   |
| 🔧 Framework independence          | Core logic doesn't depend on Quarkus, JAX-RS, CDI, etc.                    |
| 🔁 Easy evolution                  | Supports adding new entry points (REST, CLI, gRPC) with minimal changes    |
| ♻️ Extensibility via strategies    | Algorithm selection is abstracted, easy to add or replace                  |

---

### 🧩 Layered Structure

| Layer            | Responsibility                                           | Examples                                                  |
|------------------|----------------------------------------------------------|-----------------------------------------------------------|
| `adapter.in.web` | Entry point (REST API), HTTP mapping                     | `LabseqController.java`                                   |
| `application`    | Orchestration of use cases                               | `CalculateLabseqUseCase.java`                             |
| `domain`         | Core business rules and ports (interfaces)               | `LabseqStrategy.java` (interface), future domain models   |
| `infrastructure` | Technical details: strategy implementations, factories   | `ParallelLabseqStrategy.java`, `LabseqStrategyFactory.java` |
| `dto`            | Request/response models for external communication       | `LabseqResponse.java`, `ErrorResponse.java`               |
| `config`         | Exception mappers and Quarkus config                     | `IllegalArgumentExceptionMapper.java`                     |
| `util`           | Shared cache, pure utility                               | `LabseqCache.java`                                        |

---

### 🔁 Key Design Principles Applied

| Principle                    | Implementation in this project                              |
|-----------------------------|--------------------------------------------------------------|
| **Dependency Inversion**     | `application` depends on interfaces from `domain` only       |
| **Single Responsibility**    | Each class has a focused, isolated purpose                   |
| **Framework Independence**   | Strategies and core logic are decoupled from Quarkus         |
| **Loose Coupling**           | Strategy selection via factory (`LabseqStrategyFactory`)     |
| **Dependency Injection**     | CDI (`@Inject`) used for modular wiring                      |

---

### 🧭 Visual Overview

```
               +----------------------------+
               |     Adapter (REST API)     |
               |    LabseqController.java   |
               +-------------+--------------+
                             |
                             v
               +-------------+-------------+
               |       Application Layer    |
               | CalculateLabseqUseCase.java|
               +-------------+-------------+
                             |
                             v
               +-------------+-------------+
               |      Domain (Interfaces)   |
               |     LabseqStrategy.java    |
               +-------------+-------------+
                             |
                             v
               +----------------------------+
               | Infrastructure (Strategies)|
               | Iterative, Batched, Parallel|
               +----------------------------+
```

---

### ❓ Why not MVC?

| Aspect           | MVC                                             | Clean Architecture (this project)                    |
|------------------|--------------------------------------------------|------------------------------------------------------|
| Coupling         | Controller often holds logic                    | Logic is in `application` and `domain`               |
| Testability      | Difficult to isolate domain logic               | Each layer is testable in isolation                  |
| Maintainability  | Harder to evolve without breaking logic         | Easily extendable with low risk                      |
| Framework-free   | No - depends on controller logic                | Yes - logic is framework-agnostic                    |

---

### 🔧 Easily Extendable

- Add a new strategy? → just implement `LabseqStrategy` and annotate with `@Named`.
- Add another interface (CLI/gRPC)? → implement new `adapter.in.cli`, reuse `CalculateLabseqUseCase`.

---

### ⚙️ Running the Application

#### 🔧 Dev Mode

```bash
./mvnw quarkus:dev
```

Access: `http://localhost:8080/api/v1/labseq/{n}`

---

#### 📦 Build as JAR

```bash
./mvnw clean package -DskipTests
java -jar target/quarkus-app/quarkus-run.jar
```

---

### 🐳 Docker

```bash
docker build -t labseq-api .
docker run -p 8080:8080 labseq-api
```

---

### 📌 Example Endpoint

```http
GET /api/v1/labseq/1000000
```

**Response:**

```json
{
  "n": 1000000,
  "result": "195328212870775773163201494759625633244354..."
}
```

---

### 🧠 Dynamic Strategy Selection

The API automatically selects the most efficient strategy based on the input value `n`. The thresholds are configurable via `application.properties`:

```properties
labseq.threshold.iterative=1000
labseq.threshold.batched=10000
```

| `n` Range                                      | Selected Strategy   |
|------------------------------------------------|---------------------|
| `n <= 1.000 labseq.threshold.iterative`        | `iterative-labseq`  |
| `1.001 <= n <= 10.000 labseq.threshold.batched` | `labseq-batched`    |
| `n > 10.000 labseq-parallel-segmented` | `labseq-parallel-segmented` |

---

## 🇺🇸 **🔁 Available Strategies**

| Strategy Name              | Description                                                       | Parallel | Memory Use  | Best For                         |
|---------------------------|-------------------------------------------------------------------|----------|-------------|----------------------------------|
| `iterative-labseq`        | Simple loop using BigInteger                                      | ❌       | Low ✅        | Small values (≤ 1,000)           |
| `labseq-batched`          | Chunk-based incremental computation with cache reuse              | ❌       | Medium ✅     | Medium-range values (≤ 10,000)   |
| `labseq-parallel-segmented` | Block-based parallel strategy writing directly to shared cache | ✅       | Optimized ✅ | Large `n` (≥ 100,000)            |

---

### 📊 Strategy Performance Analysis

| Strategy Name              | Time Complexity | Memory Usage        | Parallel | Description                                                                     |
|---------------------------|-----------------|---------------------|----------|---------------------------------------------------------------------------------|
| `iterative-labseq`        | O(n)            | O(1)                | ❌        | Simple loop with 2 variables; efficient for small values                        |
| `labseq-batched`          | O(n)            | O(k), k = batch size| ❌        | Iterative computation in fixed-size batches; balances speed and memory          |
| `labseq-parallel-segmented` | O(n/p)        | O(k), k = block size| ✅        | Block-based parallelism with direct cache write; optimized for large sequences |

> ℹ️ Notes:
> - `O(n/p)` assumes `p` available threads (typically CPU cores).
> - `labseq-parallel-segmented` avoids holding full maps in memory.
> - All strategies use shared in-memory cache (`ConcurrentHashMap`).
> - All algorithms are **loop-based** (no recursion, no stack overflow risk).

### 📚 Resources

- [Quarkus - Official Docs](https://quarkus.io/)
- [MicroProfile Config](https://microprofile.io/project/eclipse/microprofile-config/)
- [Jakarta REST](https://jakarta.ee/specifications/restful-ws/)
- [Java BigInteger](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigInteger.html)

---