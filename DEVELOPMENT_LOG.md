# Development Log - Confluence Knowledge Assistant

This document tracks the incremental development of the Confluence Knowledge Assistant project, step by step.

## Project Overview

**Goal:** Build a small, production-style RAG system that retrieves and answers questions from a single Confluence page.

**Tech Stack:**
- Java 17
- Spring Boot 4.0.3
- Maven
- PostgreSQL (with pgvector - to be added)
- OpenAI API (to be added)
- Confluence REST API (to be added)

**Architecture:** Clean layered architecture (controller → service → repository → model)

---

## Completed Steps

### Step 1: Health Check Endpoint ✅

**Goal:** Verify Spring Boot application runs and responds to HTTP requests.

**What was added:**
- `HealthController` (`controller` layer)
  - Endpoint: `GET /health`
  - Returns: `"OK - Confluence Knowledge Assistant"`

**Files created:**
- `src/main/java/com/example/knowledge/RAGassistant/controller/HealthController.java`

**Commit:** `8b1afb3` - "Step1-just health api"

**Test:** `GET http://localhost:8080/health`

---

### Step 2: Domain Model with Lombok ✅

**Goal:** Create the core domain model for Confluence page chunks using Lombok for cleaner code.

**What was added:**
- `ConfluencePageChunk` model class (`model` layer)
  - Fields: `id` (UUID), `pageId`, `chunkIndex`, `content`, `embedding` (List<Float>), `createdAt`, `updatedAt`
  - Uses Lombok `@Data` annotation for getters/setters
- Lombok dependency in `pom.xml`

**Files created/modified:**
- `src/main/java/com/example/knowledge/RAGassistant/model/ConfluencePageChunk.java`
- `pom.xml` (added Lombok dependency)

**Commit:** `a21b6ab` - "Step 2: Add ConfluencePageChunk domain model with Lombok"

---

### Step 3: Service Layer & Sample Data API ✅

**Goal:** Demonstrate layered architecture with a service that returns sample chunk data.

**What was added:**
- `ConfluencePageChunkService` (`service` layer)
  - Method: `getSampleChunks()` - returns a hardcoded sample chunk
- `ConfluencePageChunkController` (`controller` layer)
  - Endpoint: `GET /chunks/sample`
  - Returns sample `ConfluencePageChunk` as JSON

**Files created:**
- `src/main/java/com/example/knowledge/RAGassistant/service/ConfluencePageChunkService.java`
- `src/main/java/com/example/knowledge/RAGassistant/controller/ConfluencePageChunkController.java`

**Test:** `GET http://localhost:8080/chunks/sample`

---

### Step 4: JPA Annotations & Repository Interface ✅

**Goal:** Prepare the model for database persistence with JPA annotations and create repository interface.

**What was added:**
- JPA annotations on `ConfluencePageChunk`:
  - `@Entity`, `@Table(name = "confluence_page_chunks")`
  - `@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`
  - `@Column` annotations with appropriate constraints
  - `@PrePersist` and `@PreUpdate` for automatic timestamp management
  - `embedding` column marked as `vector` type (for pgvector)
- `ConfluencePageChunkRepository` interface (`repository` layer)
  - Extends `JpaRepository<ConfluencePageChunk, UUID>`
- Spring Data JPA and PostgreSQL dependencies in `pom.xml`

**Files created/modified:**
- `src/main/java/com/example/knowledge/RAGassistant/model/ConfluencePageChunk.java` (added JPA annotations)
- `src/main/java/com/example/knowledge/RAGassistant/repository/ConfluencePageChunkRepository.java`
- `pom.xml` (added `spring-boot-starter-data-jpa` and `postgresql` dependencies)

**Commit:** `e364c87` - "Step 3-4: sample chunk API and JPA-ready model"

**Note:** Database connection not configured yet - app may not start until PostgreSQL is configured.

---

### Step 5: Database Configuration ✅

**Goal:** Wire the application to PostgreSQL so the app starts and JPA can create/update the `confluence_page_chunks` table.

**What was added:**
- Datasource and JPA properties in `application.properties`:
  - `spring.datasource.url` (default: `jdbc:postgresql://localhost:5432/confluence_rag`)
  - `spring.datasource.username` / `password` with placeholders (`${DB_USERNAME:postgres}`, `${DB_PASSWORD:postgres}`)
  - `spring.jpa.hibernate.ddl-auto=update`, PostgreSQL dialect
- `EmbeddingConverter` (converter layer): converts `List<Float>` to/from TEXT so embeddings can be stored without pgvector for now
- `ConfluencePageChunk.embedding` now uses `@Convert(converter = EmbeddingConverter.class)` and `columnDefinition = "TEXT"` (pgvector can be added in a later step)

**Files created/modified:**
- `src/main/resources/application.properties` (datasource + JPA settings)
- `src/main/java/com/example/knowledge/RAGassistant/converter/EmbeddingConverter.java`
- `src/main/java/com/example/knowledge/RAGassistant/model/ConfluencePageChunk.java` (embedding column and converter)

**How to run locally:**
1. Create a PostgreSQL database, e.g. `createdb confluence_rag`
2. Set (or use defaults) `DB_USERNAME` and `DB_PASSWORD` if needed
3. Run `mvn spring-boot:run`

**Test:** App starts without missing DataSource; table `confluence_page_chunks` is created/updated.

---

## Next Steps (Planned)

### Step 6: Minimal Confluence Integration
- Add Confluence REST API client
- Create configuration properties for Confluence credentials
- Add endpoint to fetch a single Confluence page

### Step 7: OpenAI Integration & Embeddings
- Add OpenAI client for embeddings
- Implement text chunking utility
- Create ingestion service to fetch, chunk, embed, and store Confluence content

### Step 8: RAG Query Endpoint
- Implement similarity search using pgvector
- Create RAG service that retrieves relevant chunks and generates answers
- Add question-answering REST endpoint

---

## Current Project Structure

```
src/main/java/com/example/knowledge/RAGassistant/
├── RaGassistantApplication.java          # Main Spring Boot application
├── controller/
│   ├── HealthController.java             # Health check endpoint
│   └── ConfluencePageChunkController.java # Sample chunks endpoint
├── converter/
│   └── EmbeddingConverter.java           # List<Float> <-> TEXT for embeddings
├── service/
│   └── ConfluencePageChunkService.java   # Business logic for chunks
├── repository/
│   └── ConfluencePageChunkRepository.java # JPA repository interface
└── model/
    └── ConfluencePageChunk.java          # Domain entity (JPA-ready)
```

---

## Dependencies Added So Far

- `spring-boot-starter-webmvc` - Web MVC support
- `lombok` - Code generation (getters/setters)
- `spring-boot-starter-data-jpa` - JPA/Hibernate support
- `postgresql` - PostgreSQL JDBC driver

---

*Last updated: After Step 5 completion*
