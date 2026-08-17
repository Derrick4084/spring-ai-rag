package com.derocode.rag.components;

import com.derocode.rag.configs.ChatClientConfig;
import com.derocode.rag.enums.PromptType;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PromptService.class);

    @Value("classpath:prompts/base.st")
    private Resource base;

    @Value("classpath:prompts/cart.st")
    private Resource cart;

    @Value("classpath:prompts/customer.st")
    private Resource customer;

    @Value("classpath:prompts/document.st")
    private Resource document;

    @Value("classpath:prompts/product.st")
    private Resource product;

    @Value("classpath:prompts/datetime.st")
    private Resource datetime;

    @Value("classpath:prompts/order.st")
    private Resource order;

    @Value("classpath:prompts/rag.st")
    private Resource rag;

    @Value("classpath:prompts/weather.st")
    private Resource weather;

    @Value("classpath:prompts/single.st")
    private Resource single;

    private Map<PromptType, Resource> promptMap;

    @PostConstruct
    void init() {
        promptMap = Map.of(
                PromptType.BASE, base,
                PromptType.CART, cart,
                PromptType.CUSTOMER, customer,
                PromptType.DOCUMENT, document,
                PromptType.PRODUCT, product,
                PromptType.DATETIME, datetime,
                PromptType.ORDER, order,
                PromptType.RAG, rag,
                PromptType.WEATHER, weather,
                PromptType.SINGLE, single
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


