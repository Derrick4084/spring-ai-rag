package com.derocode.rag.enums;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;

public enum PromptType {

    BASE,
    CART,
    CUSTOMER,
    DATETIME,
    DOCUMENT,
    ORDER,
    PRODUCT,
    RAG,
    WEATHER,
    SINGLE;

    public @NonNull String getPath() {
        return "classpath:prompts/" +
                name().toLowerCase() +
                ".st";
    }





}
