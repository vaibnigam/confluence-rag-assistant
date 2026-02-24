package com.example.knowledge.RAGassistant.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts List&lt;Float&gt; to TEXT for storage until pgvector is enabled.
 * Format: comma-separated floats, e.g. "0.1,0.2,-0.3"
 */
@Converter
public class EmbeddingConverter implements AttributeConverter<List<Float>, String> {

    @Override
    public String convertToDatabaseColumn(List<Float> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Float> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .map(Float::parseFloat)
                .toList();
    }
}
