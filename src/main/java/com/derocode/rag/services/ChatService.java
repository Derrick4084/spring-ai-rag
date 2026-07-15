package com.derocode.rag.services;


import com.derocode.rag.components.PromptService;
import com.derocode.rag.enums.PromptType;
import com.derocode.rag.tools.ProductTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ProductTool productTool;
    private final PromptService promptService;

    private static final Pattern PRODUCT_PATTERN = Pattern.compile(
            "\\bproduct\\b.*?(\\d+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public ChatService(ChatClient chatClient, VectorStore vectorStore, ProductTool productTool, PromptService promptService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.productTool = productTool;
        this.promptService = promptService;
    }


    public Flux<String> send(String prompt, String uuid) {


        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, uuid));

        Matcher matcher = PRODUCT_PATTERN.matcher(prompt);

        if(matcher.find()){
            requestSpec.tools(productTool);
            requestSpec.system(promptSystemSpec ->
                    promptSystemSpec.text(promptService.getSystemPrompt(
                                    PromptType.BASE,
                                    PromptType.DATETIME,
                                    PromptType.PRODUCT
                            ))
                            .param("documents","No document context was provided. "));


        } else {

            String context = retrieveContext(prompt);
            requestSpec.system(system ->
                    system.text(promptService.getSystemPrompt(
                                    PromptType.BASE,
                                    PromptType.DATETIME,
                                    PromptType.RAG,
                                    PromptType.DOCUMENT))
                            .param("documents", context));
        }
        return requestSpec
                .user(prompt)
                .stream()
                .content();
    }


    private String retrieveContext(String question) {
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        List<Document> documents = vectorStore.similaritySearch(request);

        if (documents.isEmpty()) {
            return "No relevant documents were retrieved.";
        }
        return documents.stream()
                .map(doc -> """
                    Source: %s

                    %s
                    """.formatted(
                        doc.getMetadata().getOrDefault("file_name", "Unknown"),
                        doc.getText()))
                .collect(Collectors.joining("\n\n----------------\n\n"));
    }
}



