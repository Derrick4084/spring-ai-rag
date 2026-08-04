package com.derocode.rag.configs;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class EmbeddingModelConfig {

    @Bean(name = "embeddingModel")
    @Primary
    @Profile("home")
    public EmbeddingModel ollamaEmbeddingModel(
            @Qualifier("ollamaEmbeddingModel")
            EmbeddingModel ollamaEmbeddingModel) {

        return ollamaEmbeddingModel;
    }

    @Bean(name = "embeddingModel")
    @Primary
    @Profile("remote")
    public EmbeddingModel openAiEmbeddingModel(
            @Qualifier("openAiEmbeddingModel")
            EmbeddingModel openAiEmbeddingModel) {

        return openAiEmbeddingModel;
    }






}
