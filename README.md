# confluence-rag-assistant
An internal knowledge assistant built with Spring Boot that fetches Confluence page content, generates vector embeddings, stores them in PostgreSQL using pgvector, and performs Retrieval-Augmented Generation (RAG) to provide contextual answers.

## Running locally

1. **PostgreSQL:** Create a database, e.g. `createdb confluence_rag`.
2. **Config:** Defaults in `application.properties` use `jdbc:postgresql://localhost:5432/confluence_rag`, username `postgres`, password `postgres`. Override with env vars `DB_USERNAME` and `DB_PASSWORD` if needed.
3. **Confluence:** Set environment variables (or update `application.properties`):
   - `CONFLUENCE_BASE_URL` - Your Confluence base URL (e.g., `https://your-domain.atlassian.net`)
   - `CONFLUENCE_PAGE_ID` - The Confluence page ID to fetch
   - `CONFLUENCE_USERNAME` - Your Confluence username/email
   - `CONFLUENCE_API_TOKEN` - Create at https://id.atlassian.com/manage-profile/security/api-tokens
4. **Run:** `mvn spring-boot:run`
5. **Check:** 
   - Health: `GET http://localhost:8080/health`
   - Confluence page: `GET http://localhost:8080/confluence/page`
