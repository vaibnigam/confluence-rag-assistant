package com.example.knowledge.RAGassistant.util;

import com.example.knowledge.RAGassistant.config.OpenAIProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OpenAIClient {

    private final RestTemplate restTemplate;
    private final OpenAIProperties properties;

    public OpenAIClient(@Qualifier("openAiRestTemplate") RestTemplate restTemplate, OpenAIProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public List<Float> embed(String text) {
        String url = properties.getBaseUrl() + "/embeddings";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + properties.getApiKey());
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = Map.of(
                "model", properties.getEmbeddingModel(),
                "input", text
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<EmbeddingResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                EmbeddingResponse.class
        );

        if (response.getBody() != null && !response.getBody().getData().isEmpty()) {
            return response.getBody().getData().get(0).getEmbedding();
        }

        throw new RuntimeException("Failed to get embedding from OpenAI");
    }

    @Data
    private static class EmbeddingResponse {
        private List<EmbeddingData> data;
    }

    @Data
    private static class EmbeddingData {
        @JsonProperty("embedding")
        private List<Float> embedding;
    }
}
