package com.example.knowledge.RAGassistant.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ConfluencePageChunk {

    private UUID id;
    private String pageId;
    private int chunkIndex;
    private String content;
    private List<Float> embedding;
    private Instant createdAt;
    private Instant updatedAt;
}

