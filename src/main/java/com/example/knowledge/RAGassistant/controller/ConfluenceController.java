package com.example.knowledge.RAGassistant.controller;

import com.example.knowledge.RAGassistant.service.ConfluenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confluence")
public class ConfluenceController {

    private final ConfluenceService confluenceService;

    public ConfluenceController(ConfluenceService confluenceService) {
        this.confluenceService = confluenceService;
    }

    @GetMapping("/page")
    public String getPage() {
        return confluenceService.getPageContent();
    }
}
