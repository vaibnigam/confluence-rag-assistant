package com.example.knowledge.RAGassistant.model;

import lombok.Data;

import java.util.List;

@Data
public class AnswerResponse {
    private String answer;
    private List<String> sourceChunkIds;
}
