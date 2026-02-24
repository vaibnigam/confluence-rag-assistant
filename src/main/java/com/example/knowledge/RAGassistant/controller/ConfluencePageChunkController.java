package com.example.knowledge.RAGassistant.controller;

import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;
import com.example.knowledge.RAGassistant.service.ConfluencePageChunkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chunks")
public class ConfluencePageChunkController {

    private final ConfluencePageChunkService confluencePageChunkService;

    public ConfluencePageChunkController(ConfluencePageChunkService confluencePageChunkService) {
        this.confluencePageChunkService = confluencePageChunkService;
    }

    @GetMapping("/sample")
    public Iterable<ConfluencePageChunk> getSampleChunks() {
        return confluencePageChunkService.getSampleChunks();
    }
}
