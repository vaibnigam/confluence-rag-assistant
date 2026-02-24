package com.example.knowledge.RAGassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "confluence")
public class ConfluenceProperties {

    private String baseUrl;
    private String pageId;
    private String username;
    private String apiToken;
}
