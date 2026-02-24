package com.example.knowledge.RAGassistant.controller;

import com.example.knowledge.RAGassistant.service.IngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingest")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public ResponseEntity<IngestionService.IngestionResult> ingest() {
        IngestionService.IngestionResult result = ingestionService.ingestPage();
        return ResponseEntity.ok(result);
    }
}
