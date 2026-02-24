package com.example.knowledge.RAGassistant.util;

import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;

import java.util.List;

public class SimilaritySearch {

    /**
     * Calculate cosine similarity between two vectors
     */
    public static double cosineSimilarity(List<Float> vectorA, List<Float> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.size() != vectorB.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * Find top K chunks by similarity to query embedding
     */
    public static List<ConfluencePageChunk> findTopKSimilar(
            List<ConfluencePageChunk> allChunks,
            List<Float> queryEmbedding,
            int k) {

        return allChunks.stream()
                .filter(chunk -> chunk.getEmbedding() != null)
                .map(chunk -> {
                    double similarity = cosineSimilarity(queryEmbedding, chunk.getEmbedding());
                    return new ChunkWithSimilarity(chunk, similarity);
                })
                .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
                .limit(k)
                .map(cws -> cws.chunk)
                .toList();
    }

    private static class ChunkWithSimilarity {
        final ConfluencePageChunk chunk;
        final double similarity;

        ChunkWithSimilarity(ConfluencePageChunk chunk, double similarity) {
            this.chunk = chunk;
            this.similarity = similarity;
        }
    }
}
