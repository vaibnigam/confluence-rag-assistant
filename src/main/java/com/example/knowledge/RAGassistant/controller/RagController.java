package com.example.knowledge.RAGassistant.controller;

import com.example.knowledge.RAGassistant.model.AnswerResponse;
import com.example.knowledge.RAGassistant.model.QuestionRequest;
import com.example.knowledge.RAGassistant.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/answer")
    public ResponseEntity<AnswerResponse> answerQuestion(@RequestBody QuestionRequest request) {
        AnswerResponse response = ragService.answerQuestion(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}
