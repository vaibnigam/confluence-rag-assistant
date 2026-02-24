package com.example.knowledge.RAGassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {

    private String apiKey;
    private String baseUrl = "https://api.openai.com/v1";
    private String embeddingModel = "text-embedding-3-small";
    private String chatModel = "gpt-3.5-turbo";
}
