package com.derocode.rag.interfaces;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentSplitter {

    String getExtension();

    List<Document> split(List<Document> documents);
}
