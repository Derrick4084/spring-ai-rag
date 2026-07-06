package com.derocode.rag.enums;

public enum DocumentType {
    PDF("pdf"),
    MARKDOWN("md"),
    TEXT("txt");

    private final String extension;

    DocumentType(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }


}
