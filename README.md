# confluence-rag-assistant
An internal knowledge assistant built with Spring Boot that fetches Confluence page content, generates vector embeddings, stores them in PostgreSQL using pgvector, and performs Retrieval-Augmented Generation (RAG) to provide contextual answers.

## Running locally

1. **PostgreSQL:** Create a database, e.g. `createdb confluence_rag`.
2. **Config:** Defaults in `application.properties` use `jdbc:postgresql://localhost:5432/confluence_rag`, username `postgres`, password `postgres`. Override with env vars `DB_USERNAME` and `DB_PASSWORD` if needed.
3. **Run:** `mvn spring-boot:run`
4. **Check:** `GET http://localhost:8080/health`
