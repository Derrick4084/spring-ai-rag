package com.derocode.rag.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:prompts/systemDataPrompt.st")
    private Resource template;

    public Flux<String> send(String prompt, String uuid) {

        SearchRequest request = SearchRequest.builder()
                .query(prompt)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        List<Document> documents = vectorStore.similaritySearch(request);

        String context = documents.stream().map(doc ->"""
                Source: %s
                
                
                %s
                """.formatted(
                        doc.getMetadata().getOrDefault("file_name", "Unknown"),
                        doc.getText()))
                .collect(Collectors.joining("\n\n----------------\n\n"));

        return chatClient.prompt().system(promptSystemSpec ->
                        promptSystemSpec.text(template).param("documents", context))
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, uuid))
                .user(prompt)
                .stream()
                .content();
    }
}
