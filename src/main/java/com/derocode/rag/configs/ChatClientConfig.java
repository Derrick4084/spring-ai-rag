package com.derocode.rag.configs;

import com.derocode.rag.advisors.SimpleLoggerAdvisor;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Configuration
public class ChatClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ChatClientConfig.class);

    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    private final List<McpSyncClient> mcpSyncClients;

    public ChatClientConfig(SimpleLoggerAdvisor simpleLoggerAdvisor, List<McpSyncClient> mcpSyncClients) {
        this.simpleLoggerAdvisor = simpleLoggerAdvisor;
        this.mcpSyncClients = mcpSyncClients;
    }


    @Bean
    ChatClient chatClient(ChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider)  {

        ToolCallback[] toolCallbacks = syncMcpToolCallbackProvider.getToolCallbacks();

        McpSyncClient mcpSyncClient = mcpSyncClients.getFirst();

        McpSchema.GetPromptRequest request = McpSchema.GetPromptRequest
                .builder("greeting")
                .arguments(Map.of("name", "friend"))
                .build();

        McpSchema.GetPromptResult result = mcpSyncClient.getPrompt(request);

        result.messages().forEach(message -> {
            System.out.println("Role: " + message.role());
            System.out.println("Content: " + message.content());
        });

        ToolCallbackProvider provider = ToolCallbackProvider.from(toolCallbacks);

        for(ToolCallback toolCallback : toolCallbacks){
            System.out.println("Tool name: " +  toolCallback.getToolDefinition().name());
        }

        return ChatClient.builder(chatModel)
                .defaultAdvisors(simpleLoggerAdvisor)
                .defaultTools(provider)
                .build();
    }


//    @Bean
//    OpenAiHttpClientBuilderCustomizer debugCustomizer() {
//        return builder -> builder
//                .interceptor(chain -> {
//                    log.info("OpenAI request started: {}", chain.request().url());
//                    log.info(
//                            "Call timeout: {} ms",
//                            chain.call().timeout().timeoutNanos() / 1_000_000
//                    );
//                    log.info(
//                            "Read timeout: {} ms",
//                            chain.readTimeoutMillis()
//                    );
//                    Response response = chain.proceed(chain.request());
//                    log.info(
//                            "OpenAI response: code={}, protocol={}, headers={}",
//                            response.code(),
//                            response.protocol(),
//                            response.headers()
//                    );
//                   return response;
//                });
//    }
}
