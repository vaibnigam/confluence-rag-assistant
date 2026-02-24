package com.example.knowledge.RAGassistant.service;

import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

@Service
public class ConfluencePageChunkService {

    public Iterable<ConfluencePageChunk> getSampleChunks() {
        ConfluencePageChunk chunk = new ConfluencePageChunk();
        chunk.setId(UUID.randomUUID());
        chunk.setPageId("sample-page-id");
        chunk.setChunkIndex(0);
        chunk.setContent("This is a sample Confluence page chunk.");
        chunk.setCreatedAt(Instant.now());
        chunk.setUpdatedAt(Instant.now());
        // embedding left null for now

        return Collections.singletonList(chunk);
    }
}
