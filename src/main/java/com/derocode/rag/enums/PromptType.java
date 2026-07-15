package com.derocode.rag.enums;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;

public enum PromptType {

    BASE,
    DATETIME,
    DOCUMENT,
    PRODUCT,
    RAG;

    public @NonNull String getPath() {
        return "classpath:prompts/" +
                name().toLowerCase() +
                ".st";
    }





}
