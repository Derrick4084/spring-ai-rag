package com.derocode.rag.interfaces;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentReader {

    String getSupportedExtension();

    List<Document> read(Resource resource);
}
