package com.example.knowledge.RAGassistant.repository;

import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConfluencePageChunkRepository extends JpaRepository<ConfluencePageChunk, UUID> {
}
