package com.derocode.rag.services;


import com.derocode.rag.components.PromptService;
import com.derocode.rag.enums.PromptType;
import com.derocode.rag.records.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final PromptService promptService;

    public ChatService(ChatClient chatClient, PromptService promptService) {
        this.chatClient = chatClient;
        this.promptService = promptService;
    }

    public Flux<String> sendStream(String prompt, String uuid, String timezone) {

        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, uuid));

        return requestSpec.system(promptSystemSpec -> promptSystemSpec.text(
                promptService.getSystemPrompt(
                        PromptType.BASE,
                        PromptType.PRODUCT,
                        PromptType.DATETIME,
                        PromptType.RAG,
                        PromptType.WEATHER
                ))
                        .param("timezone", timezone)
                )
                .user(prompt)
                .stream()
                .content();
//                .doOnSubscribe(s -> log.info("STREAM START"))
//                .doOnNext(t -> log.debug("TOKEN [{}]", t))
//                .doOnComplete(() -> log.info("STREAM COMPLETE"))
//                .doOnCancel(() -> log.warn("STREAM CANCEL"))
//                .doOnError(e -> log.error("STREAM ERROR", e))
//                .doFinally(signal -> log.info("STREAM FINALLY {}", signal));


    }


    public String sendCall(String prompt, String uuid, String timezone){


        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, uuid));

        return requestSpec.system(promptSystemSpec -> promptSystemSpec.text(
                                promptService.getSystemPrompt(
                                        PromptType.BASE,
                                        PromptType.PRODUCT,
                                        PromptType.DATETIME,
                                        PromptType.RAG,
                                        PromptType.WEATHER
                                ))
                        .param("timezone", timezone)
                )
                .user(prompt)
                .call()
                .content();



    }
}



