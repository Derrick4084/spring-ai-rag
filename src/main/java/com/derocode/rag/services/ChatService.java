package com.derocode.rag.services;


import com.derocode.rag.components.PromptService;
import com.derocode.rag.enums.PromptType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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


    public String sendCall(String prompt, String uuid, String timezone){

        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, uuid));

        return requestSpec.system(promptSystemSpec -> promptSystemSpec.text(
                                promptService.getSystemPrompt(
                                        PromptType.BASE,
                                        PromptType.CART,
                                        PromptType.CUSTOMER,
                                        PromptType.PRODUCT,
                                        PromptType.DATETIME,
                                        PromptType.ORDER,
                                        PromptType.RAG,
                                        PromptType.WEATHER
                                ))
                        .param("timezone", timezone)
                )
                .user(prompt)
                .call()
                .content();

    }






    public Flux<String> sendStream(String prompt, String uuid, String timezone) {

        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, uuid));

        return requestSpec.system(promptSystemSpec -> promptSystemSpec.text(
                promptService.getSystemPrompt(
                        PromptType.BASE,
                        PromptType.CART,
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
}



