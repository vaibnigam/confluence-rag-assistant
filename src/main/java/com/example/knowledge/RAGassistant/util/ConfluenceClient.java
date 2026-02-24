package com.example.knowledge.RAGassistant.util;

import com.example.knowledge.RAGassistant.config.ConfluenceProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class ConfluenceClient {

    private final RestTemplate restTemplate;
    private final ConfluenceProperties properties;

    public ConfluenceClient(RestTemplate restTemplate, ConfluenceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public String fetchPageContent() {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getBaseUrl())
                .path("/rest/api/content/{pageId}")
                .queryParam("expand", "body.storage,version")
                .buildAndExpand(properties.getPageId())
                .toUriString();

        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        if (body != null) {
            Map<String, Object> pageBody = (Map<String, Object>) body.get("body");
            if (pageBody != null) {
                Map<String, Object> storage = (Map<String, Object>) pageBody.get("storage");
                if (storage != null) {
                    return (String) storage.get("value");
                }
            }
        }

        return null;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = properties.getUsername() + ":" + properties.getApiToken();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Accept", "application/json");
        return headers;
    }
}
