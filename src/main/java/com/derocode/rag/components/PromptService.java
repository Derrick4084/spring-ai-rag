package com.derocode.rag.components;

import com.derocode.rag.enums.PromptType;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PromptService {

    @Value("classpath:prompts/base.st")
    private Resource base;

    @Value("classpath:prompts/document.st")
    private Resource document;

    @Value("classpath:prompts/product.st")
    private Resource product;

    @Value("classpath:prompts/datetime.st")
    private Resource datetime;

    @Value("classpath:prompts/rag.st")
    private Resource rag;

    private Map<PromptType, Resource> promptMap;

    @PostConstruct
    void init() {
        promptMap = Map.of(
                PromptType.BASE, base,
                PromptType.DOCUMENT, document,
                PromptType.PRODUCT, product,
                PromptType.DATETIME, datetime,
                PromptType.RAG, rag
        );
    }


    public String getSystemPrompt(PromptType... prompts) {

        return Arrays.stream(prompts)
                .map(promptMap::get)
                .map(this::read)
                .collect(
                        Collectors.joining("\n\n")
                );
    }

    private String read(@NonNull Resource resource) {
        try {
            return new String(resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


