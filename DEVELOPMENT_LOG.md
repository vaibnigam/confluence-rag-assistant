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

### Step 6: Minimal Confluence Integration ✅

**Goal:** Fetch content from a single Confluence page using the Confluence REST API and expose it via a REST endpoint.

**What was added:**
- `ConfluenceProperties` (config layer): `@ConfigurationProperties(prefix = "confluence")` for base URL, page ID, username, and API token
- `ConfluenceConfig` (config layer): Provides `RestTemplate` bean for HTTP calls to Confluence
- `ConfluenceClient` (util layer): REST API client that:
  - Builds Confluence REST API URL for the configured page ID
  - Uses Basic Auth (username + API token)
  - Fetches page content with `expand=body.storage,version`
  - Extracts the storage body content
- `ConfluenceService` (service layer): Wraps `ConfluenceClient` and provides basic HTML tag cleanup
- `ConfluenceController` (controller layer): `GET /confluence/page` endpoint that returns page content
- Confluence properties in `application.properties` with placeholders for environment variables

**Files created:**
- `src/main/java/com/example/knowledge/RAGassistant/config/ConfluenceProperties.java`
- `src/main/java/com/example/knowledge/RAGassistant/config/ConfluenceConfig.java`
- `src/main/java/com/example/knowledge/RAGassistant/util/ConfluenceClient.java`
- `src/main/java/com/example/knowledge/RAGassistant/service/ConfluenceService.java`
- `src/main/java/com/example/knowledge/RAGassistant/controller/ConfluenceController.java`
- `src/main/resources/application.properties` (added confluence.* properties)

**Configuration:**
Set these environment variables or update `application.properties`:
- `CONFLUENCE_BASE_URL` (e.g., `https://your-domain.atlassian.net`)
- `CONFLUENCE_PAGE_ID` (the Confluence page ID to fetch)
- `CONFLUENCE_USERNAME` (your Confluence username/email)
- `CONFLUENCE_API_TOKEN` (Confluence API token - create at https://id.atlassian.com/manage-profile/security/api-tokens)

**Test:** `GET http://localhost:8080/confluence/page` returns the content of the configured Confluence page.

---

### Step 7: OpenAI Integration & Embeddings ✅

**Goal:** Integrate with OpenAI for embeddings, implement text chunking, and create an ingestion pipeline that fetches Confluence content, chunks it, generates embeddings, and stores chunks in the database.

**What was added:**
- `OpenAIProperties` (config layer): `@ConfigurationProperties(prefix = "openai")` for API key, base URL, embedding model, and chat model
- `OpenAIConfig` (config layer): Provides dedicated `RestTemplate` bean for OpenAI API calls with longer timeouts
- `OpenAIClient` (util layer): REST API client that:
  - Calls OpenAI embeddings API (`/embeddings`)
  - Takes text input and returns `List<Float>` embedding vector
  - Uses Bearer token authentication
- `TextChunker` (util layer): Utility class that:
  - Splits large text into smaller chunks (default: 1000 chars with 200 char overlap)
  - Tries to break at sentence boundaries when possible
  - Returns list of text chunks
- `IngestionService` (service layer): Orchestrates the full ingestion pipeline:
  - Fetches page content from Confluence
  - Chunks the content using `TextChunker`
  - Deletes existing chunks for the page
  - For each chunk: calls OpenAI to get embedding, creates `ConfluencePageChunk` entity, saves to database
  - Returns `IngestionResult` with counts of saved/total chunks
- `IngestionController` (controller layer): `POST /ingest` endpoint that triggers ingestion
- `ConfluencePageChunkRepository`: Added `deleteByPageId()` method
- OpenAI properties in `application.properties` with placeholders for environment variables

**Files created:**
- `src/main/java/com/example/knowledge/RAGassistant/config/OpenAIProperties.java`
- `src/main/java/com/example/knowledge/RAGassistant/config/OpenAIConfig.java`
- `src/main/java/com/example/knowledge/RAGassistant/util/OpenAIClient.java`
- `src/main/java/com/example/knowledge/RAGassistant/util/TextChunker.java`
- `src/main/java/com/example/knowledge/RAGassistant/service/IngestionService.java`
- `src/main/java/com/example/knowledge/RAGassistant/controller/IngestionController.java`
- `src/main/resources/application.properties` (added openai.* properties)
- `src/main/java/com/example/knowledge/RAGassistant/repository/ConfluencePageChunkRepository.java` (added deleteByPageId method)

**Configuration:**
Set these environment variables or update `application.properties`:
- `OPENAI_API_KEY` - Your OpenAI API key (required)
- `OPENAI_BASE_URL` - Optional, defaults to `https://api.openai.com/v1`
- `OPENAI_EMBEDDING_MODEL` - Optional, defaults to `text-embedding-3-small`
- `OPENAI_CHAT_MODEL` - Optional, defaults to `gpt-3.5-turbo` (for future use)

**Test:** 
1. Ensure Confluence and OpenAI credentials are configured
2. `POST http://localhost:8080/ingest` triggers ingestion
3. Returns JSON with `savedChunks` and `totalChunks` counts
4. Check database: `confluence_page_chunks` table should contain chunks with embeddings

---

### Step 8: RAG Query Endpoint ✅

**Goal:** Implement the complete RAG flow: given a question, embed it, retrieve the most similar chunks using similarity search, construct a prompt with context, and generate an answer using OpenAI chat completion.

**What was added:**
- `QuestionRequest` (model layer): DTO with `question` field
- `AnswerResponse` (model layer): DTO with `answer` and `sourceChunkIds` fields
- `SimilaritySearch` (util layer): Utility class that:
  - Calculates cosine similarity between two embedding vectors
  - Finds top K chunks by similarity to a query embedding
- `RetrievalService` (service layer): Retrieves relevant chunks by:
  - Loading all chunks from database
  - Using `SimilaritySearch` to find top K most similar chunks to question embedding
- `RagService` (service layer): Orchestrates the full RAG flow:
  - Embeds the question using `OpenAIClient`
  - Retrieves top 3 relevant chunks using `RetrievalService`
  - Builds context string from chunk contents
  - Constructs system and user prompts
  - Calls OpenAI chat completion API via `OpenAIClient.chat()`
  - Returns `AnswerResponse` with answer and source chunk IDs
- `RagController` (controller layer): `POST /rag/answer` endpoint that:
  - Accepts `QuestionRequest` JSON
  - Delegates to `RagService.answerQuestion()`
  - Returns `AnswerResponse` JSON
- `OpenAIClient`: Added `chat()` method for chat completions API

**Files created:**
- `src/main/java/com/example/knowledge/RAGassistant/model/QuestionRequest.java`
- `src/main/java/com/example/knowledge/RAGassistant/model/AnswerResponse.java`
- `src/main/java/com/example/knowledge/RAGassistant/util/SimilaritySearch.java`
- `src/main/java/com/example/knowledge/RAGassistant/service/RetrievalService.java`
- `src/main/java/com/example/knowledge/RAGassistant/service/RagService.java`
- `src/main/java/com/example/knowledge/RAGassistant/controller/RagController.java`
- `src/main/java/com/example/knowledge/RAGassistant/util/OpenAIClient.java` (added chat method)

**Test:**
1. Ensure ingestion has been run (`POST /ingest`) to populate chunks
2. `POST http://localhost:8080/rag/answer` with JSON body:
   ```json
   {
     "question": "What is this document about?"
   }
   ```
3. Returns JSON with `answer` and `sourceChunkIds` array

**Note:** Currently uses cosine similarity in Java since embeddings are stored as TEXT. Can be upgraded to pgvector for better performance in production.

---

## Project Complete! ✅

All planned steps have been implemented. The RAG system is now fully functional:
- ✅ Confluence integration (fetch pages)
- ✅ OpenAI integration (embeddings + chat)
- ✅ Text chunking
- ✅ Ingestion pipeline (fetch → chunk → embed → store)
- ✅ Similarity search
- ✅ Question-answering API

---

## Current Project Structure

```
src/main/java/com/example/knowledge/RAGassistant/
├── RaGassistantApplication.java          # Main Spring Boot application
├── controller/
│   ├── HealthController.java             # Health check endpoint
│   ├── ConfluencePageChunkController.java # Sample chunks endpoint
│   ├── ConfluenceController.java        # Confluence page content endpoint
│   ├── IngestionController.java         # Ingestion endpoint
│   └── RagController.java               # RAG question-answering endpoint
├── config/
│   ├── ConfluenceProperties.java        # Confluence configuration properties
│   ├── ConfluenceConfig.java            # RestTemplate bean configuration
│   ├── OpenAIProperties.java           # OpenAI configuration properties
│   └── OpenAIConfig.java                # OpenAI RestTemplate bean configuration
├── converter/
│   └── EmbeddingConverter.java           # List<Float> <-> TEXT for embeddings
├── util/
│   ├── ConfluenceClient.java            # Confluence REST API client
│   ├── OpenAIClient.java                # OpenAI embeddings + chat API client
│   ├── TextChunker.java                 # Text chunking utility
│   └── SimilaritySearch.java           # Cosine similarity search utility
├── service/
│   ├── ConfluencePageChunkService.java   # Business logic for chunks
│   ├── ConfluenceService.java           # Confluence page fetching service
│   ├── IngestionService.java            # Ingestion orchestration service
│   ├── RetrievalService.java           # Similarity search service
│   └── RagService.java                 # RAG orchestration service
├── repository/
│   └── ConfluencePageChunkRepository.java # JPA repository interface
└── model/
    ├── ConfluencePageChunk.java          # Domain entity (JPA-ready)
    ├── QuestionRequest.java              # Question DTO
    └── AnswerResponse.java               # Answer DTO
```

---

## Dependencies Added So Far

- `spring-boot-starter-webmvc` - Web MVC support
- `lombok` - Code generation (getters/setters)
- `spring-boot-starter-data-jpa` - JPA/Hibernate support
- `postgresql` - PostgreSQL JDBC driver

---

*Last updated: After Step 8 completion - Project Complete!*
