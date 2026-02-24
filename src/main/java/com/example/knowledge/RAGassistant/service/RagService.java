package com.example.knowledge.RAGassistant.service;

import com.example.knowledge.RAGassistant.model.AnswerResponse;
import com.example.knowledge.RAGassistant.model.ConfluencePageChunk;
import com.example.knowledge.RAGassistant.util.OpenAIClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final OpenAIClient openAIClient;
    private final RetrievalService retrievalService;
    private static final int TOP_K = 3;

    public RagService(OpenAIClient openAIClient, RetrievalService retrievalService) {
        this.openAIClient = openAIClient;
        this.retrievalService = retrievalService;
    }

    public AnswerResponse answerQuestion(String question) {
        // Step 1: Embed the question
        List<Float> questionEmbedding = openAIClient.embed(question);

        // Step 2: Retrieve relevant chunks
        List<ConfluencePageChunk> relevantChunks = retrievalService.retrieveRelevantChunks(questionEmbedding, TOP_K);

        if (relevantChunks.isEmpty()) {
            AnswerResponse response = new AnswerResponse();
            response.setAnswer("I couldn't find any relevant information to answer your question.");
            response.setSourceChunkIds(List.of());
            return response;
        }

        // Step 3: Build context from chunks
        String context = relevantChunks.stream()
                .map(ConfluencePageChunk::getContent)
                .collect(Collectors.joining("\n\n"));

        // Step 4: Build prompt
        String systemPrompt = "You are a helpful assistant that answers questions based on the provided context from Confluence documentation. " +
                "Answer the question using only the information from the context. " +
                "If the context doesn't contain enough information to answer the question, say so. " +
                "Be concise and accurate.";

        String userPrompt = "Context:\n" + context + "\n\nQuestion: " + question;

        // Step 5: Get answer from OpenAI
        String answer = openAIClient.chat(systemPrompt, userPrompt);

        // Step 6: Build response
        AnswerResponse response = new AnswerResponse();
        response.setAnswer(answer);
        response.setSourceChunkIds(relevantChunks.stream()
                .map(chunk -> chunk.getId().toString())
                .collect(Collectors.toList()));

        return response;
    }
}
