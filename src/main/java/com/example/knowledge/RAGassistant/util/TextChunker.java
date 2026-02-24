package com.example.knowledge.RAGassistant.util;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_OVERLAP = 200;

    public static List<String> chunk(String text) {
        return chunk(text, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    public static List<String> chunk(String text, int chunkSize, int overlap) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        int textLength = text.length();

        if (textLength <= chunkSize) {
            return List.of(text.trim());
        }

        int start = 0;
        while (start < textLength) {
            int end = Math.min(start + chunkSize, textLength);

            // Try to break at sentence boundary if possible
            if (end < textLength) {
                int lastPeriod = text.lastIndexOf('.', end);
                int lastNewline = text.lastIndexOf('\n', end);
                int breakPoint = Math.max(lastPeriod, lastNewline);

                if (breakPoint > start + chunkSize / 2) {
                    end = breakPoint + 1;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            start = end - overlap;
            if (start >= textLength) {
                break;
            }
        }

        return chunks;
    }
}
