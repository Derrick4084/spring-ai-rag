package com.derocode.rag.splitters;

import com.derocode.rag.enums.DocumentType;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarkDownSplitter extends AbstractTokenDocumentSplitter {
    @Override
    public String getExtension() {
        return DocumentType.MARKDOWN.extension();
    }

    @Override
    public List<Document> split(List<Document> documents) {
        return split(documents,400,250,50);
    }
}
