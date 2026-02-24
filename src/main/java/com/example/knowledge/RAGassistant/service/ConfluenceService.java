package com.example.knowledge.RAGassistant.service;

import com.example.knowledge.RAGassistant.util.ConfluenceClient;
import org.springframework.stereotype.Service;

@Service
public class ConfluenceService {

    private final ConfluenceClient confluenceClient;

    public ConfluenceService(ConfluenceClient confluenceClient) {
        this.confluenceClient = confluenceClient;
    }

    public String getPageContent() {
        String content = confluenceClient.fetchPageContent();
        if (content == null) {
            return "Unable to fetch Confluence page content.";
        }
        // Basic cleanup - remove HTML tags for now (simple approach)
        return content.replaceAll("<[^>]+>", "").trim();
    }
}
