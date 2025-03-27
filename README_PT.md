⬅️ [Back](README.md)

## 📘 Labseq API - Quarkus Project

Esta é uma API REST criada com **Quarkus 3.19.4** e **Java 21**, projetada com **Clean Architecture** para cálculos eficientes da sequência de Labseq, mesmo com valores extremamente grandes. A aplicação utiliza **estratégias otimizadas e adaptativas**, com uso de **BigInteger**, **threading** e **memoização em memória** para alta performance.

---

### 🚀 Features

- API HTTP com Quarkus
- Cálculo eficiente usando `BigInteger`
- Estratégias separadas com injeção dinâmica (`@Named`)
- Escolha automática da melhor estratégia conforme `n`
- Cache em memória com `ConcurrentHashMap`
- Arquitetura limpa e separação de camadas
- Preparado para Docker

---

### 🧪 Tech Stack

- Java 21
- Quarkus 3.19.4
- JAX-RS (Jakarta REST)
- CDI (`jakarta.inject`)
- BigInteger
- Docker

---

# 📦 Estrutura do Projeto

## 🧱 Arquitetura – Clean Architecture (orientada a REST com Quarkus)

Este projeto aplica os princípios da **Clean Architecture**, adaptados para uma API REST em **Quarkus**. O objetivo é manter separação de responsabilidades, baixo acoplamento e alta escalabilidade — mesmo em um serviço simples como o cálculo da sequência de Labseq.

---

### ✅ Por que Clean Architecture?

Ao invés da estrutura tradicional MVC (Model-View-Controller), este projeto segue a Clean Architecture para garantir:

| Benefício                        | Descrição                                                                 |
|----------------------------------|---------------------------------------------------------------------------|
| 🧼 Separação de responsabilidades | Camadas distintas para lógica de negócio, orquestração e interface       |
| 🧪 Alta testabilidade             | Cada camada é facilmente testável de forma isolada                       |
| 🔧 Independência de framework     | A lógica central não depende de Quarkus, JAX-RS, CDI, etc.               |
| 🔁 Evolução facilitada           | Fácil adicionar novas interfaces (REST, CLI, gRPC) sem alterar a lógica  |
| ♻️ Extensibilidade por estratégias| Algoritmos desacoplados via Strategy Pattern                             |

---

### 🧩 Estrutura em camadas

| Camada            | Responsabilidade                                              | Exemplos                                                     |
|-------------------|---------------------------------------------------------------|--------------------------------------------------------------|
| `adapter.in.web`  | Ponto de entrada (API REST), mapeamento HTTP                  | `LabseqController.java`                                      |
| `application`     | Orquestração dos casos de uso                                 | `CalculateLabseqUseCase.java`                                |
| `domain`          | Regras de negócio centrais e portas (interfaces)              | `LabseqStrategy.java` (interface), possíveis modelos de domínio |
| `infrastructure`  | Detalhes técnicos: estratégias e fábricas                     | `ParallelLabseqStrategy.java`, `LabseqStrategyFactory.java`  |
| `dto`             | Modelos de requisição e resposta                              | `LabseqResponse.java`, `ErrorResponse.java`                  |
| `config`          | Mapeamento de exceções e configuração Quarkus                 | `IllegalArgumentExceptionMapper.java`                        |
| `util`            | Cache compartilhado, utilitários puros                        | `LabseqCache.java`                                           |

---

### 🔁 Princípios de design aplicados

| Princípio                   | Implementação neste projeto                                  |
|-----------------------------|---------------------------------------------------------------|
| **Inversão de dependência** | `application` depende apenas de interfaces de `domain`        |
| **Responsabilidade única**  | Cada classe tem uma responsabilidade clara e isolada          |
| **Independência de framework** | Estratégias e lógica central desacopladas de Quarkus       |
| **Baixo acoplamento**       | Estratégias selecionadas via fábrica (`LabseqStrategyFactory`)|
| **Injeção de dependência**  | Modularidade via CDI (`@Inject`)                              |

---

### 🧭 Visão em blocos

```
               +----------------------------+
               |    Adaptador (API REST)    |
               |   LabseqController.java    |
               +-------------+--------------+
                             |
                             v
               +-------------+-------------+
               |   Camada de Aplicação     |
               | CalculateLabseqUseCase.java |
               +-------------+-------------+
                             |
                             v
               +-------------+-------------+
               |    Domínio (Interfaces)   |
               |    LabseqStrategy.java    |
               +-------------+-------------+
                             |
                             v
               +----------------------------+
               | Infraestrutura (Estratégias)|
               | Iterativa, Batched, Paralela|
               +----------------------------+
```

---

### ❓ Por que não usar MVC?

| Aspecto          | MVC Tradicional                                 | Clean Architecture (este projeto)                |
|------------------|--------------------------------------------------|--------------------------------------------------|
| Acoplamento      | Controller costuma conter lógica                 | Lógica isolada em `application` e `domain`       |
| Testabilidade    | Difícil isolar lógica de domínio                 | Cada camada testável de forma independente       |
| Manutenibilidade | Difícil evoluir sem quebrar lógica               | Facilmente extensível com baixo risco            |
| Independência    | ❌ Depende do controller                          | ✅ Lógica independente de framework               |

---

### 🔧 Fácil de extender

- Quer adicionar uma nova estratégia? → implemente `LabseqStrategy` e adicione `@Named`.
- Precisa de uma nova interface (CLI, gRPC)? → crie um novo `adapter.in.cli` e reutilize `CalculateLabseqUseCase`.

---

### ⚙️ Executando a Aplicação

#### 🔧 Modo Dev

```bash
./mvnw quarkus:dev
```

Acesse: `http://localhost:8080/api/v1/labseq/{n}`

---

#### 📦 Build como JAR

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

### 📌 Endpoint Exemplo

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

### 🧠 Estratégia Automática

A aplicação escolhe a melhor estratégia com base no valor de `n`, com limiares configuráveis em `application.properties`:

```properties
labseq.threshold.iterative=1000
labseq.threshold.batched=10000
```

| Faixa de `n`                  | Estratégia aplicada |
|------------------------------|------------------|
| `n <= 1.000 labseq.threshold.iterative` | `iterative-labseq` |
| `1.001 <= n <= 10.000 labseq.threshold.batched`   | `labseq-batched` |
| `n > 10.000 labseq-parallel-segmented `    | `labseq-parallel-segmented` |

---

### 🔁 Estratégias Disponíveis

| Nome da Estratégia              | Descrição                                                       | Paralelo | Uso de Memória | Ideal para                        |
|--------------------------------|------------------------------------------------------------------|----------|----------------|-----------------------------------|
| `iterative-labseq`             | Laço simples com BigInteger                                     | ❌       | Baixo ✅         | Valores pequenos (≤ 1.000)        |
| `labseq-batched`               | Cálculo incremental por lotes com cache                         | ❌       | Médio ✅         | Faixa média de valores (≤ 10,000) |
| `labseq-parallel-segmented`   | Estratégia paralela por blocos com escrita direta no cache      | ✅       | Otimizado ✅     | Cálculos grandes (≥ 100.000)      |

---

### 📊 Análise de Desempenho das Estratégias

| Nome da Estratégia           | Complexidade     | Uso de Memória         | Paralelo | Descrição                                                                       |
|-----------------------------|------------------|-------------------------|----------|----------------------------------------------------------------------------------|
| `iterative-labseq`          | O(n)             | O(1)                    | ❌        | Laço simples com 2 variáveis; ótimo para valores pequenos                       |
| `labseq-batched`            | O(n)             | O(k), k = tamanho do lote| ❌       | Processa em lotes fixos; bom equilíbrio entre velocidade e memória              |
| `labseq-parallel-segmented` | O(n/p)           | O(k), k = tamanho do bloco| ✅     | Paralelismo por blocos com gravação direta no cache; eficiente para `n` altos   |

> ℹ️ Notas:
> - `O(n/p)` assume `p` threads disponíveis (ex: núcleos da CPU).
> - A versão segmentada não guarda blocos inteiros em memória.
> - Todas as estratégias usam `ConcurrentHashMap` como cache compartilhado.
> - Nenhuma usa recursão (zero risco de estouro de pilha).


### 📚 Recursos

- [Quarkus - Official Docs](https://quarkus.io/)
- [MicroProfile Config](https://microprofile.io/project/eclipse/microprofile-config/)
- [Jakarta REST](https://jakarta.ee/specifications/restful-ws/)
- [Java BigInteger](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigInteger.html)

---