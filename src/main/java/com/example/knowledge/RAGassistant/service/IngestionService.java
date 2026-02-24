package com.example.knowledge.RAGassistant.service;

import com.example.knowledge.RAGassistant.config.ConfluenceProperties;
import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;
import com.example.knowledge.RAGassistant.repository.ConfluencePageChunkRepository;
import com.example.knowledge.RAGassistant.util.OpenAIClient;
import com.example.knowledge.RAGassistant.util.TextChunker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class IngestionService {

    private final ConfluenceService confluenceService;
    private final OpenAIClient openAIClient;
    private final ConfluencePageChunkRepository repository;
    private final ConfluenceProperties confluenceProperties;

    public IngestionService(
            ConfluenceService confluenceService,
            OpenAIClient openAIClient,
            ConfluencePageChunkRepository repository,
            ConfluenceProperties confluenceProperties) {
        this.confluenceService = confluenceService;
        this.openAIClient = openAIClient;
        this.repository = repository;
        this.confluenceProperties = confluenceProperties;
    }

    @Transactional
    public IngestionResult ingestPage() {
        // Fetch page content from Confluence
        String pageContent = confluenceService.getPageContent();
        if (pageContent == null || pageContent.isBlank()) {
            throw new RuntimeException("Failed to fetch Confluence page content");
        }

        // Chunk the content
        List<String> chunks = TextChunker.chunk(pageContent);

        // Delete existing chunks for this page (if any)
        String pageId = getPageId();
        repository.deleteByPageId(pageId);

        // Embed and save each chunk
        int savedCount = 0;
        for (int i = 0; i < chunks.size(); i++) {
            try {
                String chunkText = chunks.get(i);
                List<Float> embedding = openAIClient.embed(chunkText);

                ConfluencePageChunk chunk = new ConfluencePageChunk();
                chunk.setId(UUID.randomUUID());
                chunk.setPageId(pageId);
                chunk.setChunkIndex(i);
                chunk.setContent(chunkText);
                chunk.setEmbedding(embedding);

                repository.save(chunk);
                savedCount++;
            } catch (Exception e) {
                // Log error but continue with other chunks
                System.err.println("Failed to process chunk " + i + ": " + e.getMessage());
            }
        }

        return new IngestionResult(savedCount, chunks.size());
    }

    private String getPageId() {
        return confluenceProperties.getPageId();
    }

    public static class IngestionResult {
        private final int savedChunks;
        private final int totalChunks;

        public IngestionResult(int savedChunks, int totalChunks) {
            this.savedChunks = savedChunks;
            this.totalChunks = totalChunks;
        }

        public int getSavedChunks() {
            return savedChunks;
        }

        public int getTotalChunks() {
            return totalChunks;
        }
    }
}
