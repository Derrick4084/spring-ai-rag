package com.derocode.rag.configs;

import com.derocode.rag.tools.DateTimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ChatClientConfig {

    private final DateTimeTool dateTimeTool;

    public ChatClientConfig(DateTimeTool dateTimeTool) {
        this.dateTimeTool = dateTimeTool;
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel) {

        return ChatClient.builder(chatModel)
                .defaultTools(dateTimeTool)
                .build();
    }

}
