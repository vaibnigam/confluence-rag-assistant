package com.example.knowledge.RAGassistant.service;

import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;
import com.example.knowledge.RAGassistant.repository.ConfluencePageChunkRepository;
import com.example.knowledge.RAGassistant.util.SimilaritySearch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalService {

    private final ConfluencePageChunkRepository repository;

    public RetrievalService(ConfluencePageChunkRepository repository) {
        this.repository = repository;
    }

    public List<ConfluencePageChunk> retrieveRelevantChunks(List<Float> questionEmbedding, int k) {
        List<ConfluencePageChunk> allChunks = repository.findAll();
        return SimilaritySearch.findTopKSimilar(allChunks, questionEmbedding, k);
    }
}
