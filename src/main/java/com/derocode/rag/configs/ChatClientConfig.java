package com.derocode.rag.configs;

import com.derocode.rag.advisors.SimpleLoggerAdvisor;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.List;
import java.util.Map;


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
    @Profile("remote")
    public ChatClient openAiChatClient(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider syncMcpToolCallbackProvider){
        ToolCallback[] toolCallbacks = syncMcpToolCallbackProvider.getToolCallbacks();
//        McpSyncClient mcpSyncClient = mcpSyncClients.getFirst();
//        McpSchema.GetPromptRequest request = McpSchema.GetPromptRequest
//                .builder("greeting")
//                .arguments(Map.of("name", "friend"))
//                .build();
//        McpSchema.GetPromptResult result = mcpSyncClient.getPrompt(request);
//        result.messages().forEach(message -> {
//            log.info("Role: {}", message.role());
//            log.info("Content: {}", message.content());
//        });
        ToolCallbackProvider provider = ToolCallbackProvider.from(toolCallbacks);
//        for(ToolCallback toolCallback : toolCallbacks){
//            log.info("Tool name: {}", toolCallback.getToolDefinition().name());
//            log.info("Tool schema: {}", toolCallback.getToolDefinition().inputSchema());
//        }
        return ChatClient.builder(chatModel)
//                .defaultAdvisors(simpleLoggerAdvisor)
                .defaultTools(provider)
                .build();
    }

    @Bean
    @Profile("home")
    public ChatClient ollamaChatClient(OllamaChatModel chatModel, @NonNull SyncMcpToolCallbackProvider syncMcpToolCallbackProvider){
        ToolCallback[] toolCallbacks = syncMcpToolCallbackProvider.getToolCallbacks();
//        McpSyncClient mcpSyncClient = mcpSyncClients.getFirst();
//        McpSchema.GetPromptRequest request = McpSchema.GetPromptRequest
//                .builder("greeting")
//                .arguments(Map.of("name", "friend"))
//                .build();
//        McpSchema.GetPromptResult result = mcpSyncClient.getPrompt(request);
//        result.messages().forEach(message -> {
//            log.info("Role: {}", message.role());
//            log.info("Content: {}", message.content());
//        });
        ToolCallbackProvider provider = ToolCallbackProvider.from(toolCallbacks);
//        for(ToolCallback toolCallback : toolCallbacks){
//            log.info("Tool name: {}", toolCallback.getToolDefinition().name());
//            log.info("Tool schema: {}", toolCallback.getToolDefinition().inputSchema());
//        }
        return ChatClient.builder(chatModel)
//                .defaultAdvisors(simpleLoggerAdvisor)
                .defaultTools(provider)
                .build();
    }
}
