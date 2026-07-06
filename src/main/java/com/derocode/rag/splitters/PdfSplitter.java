package com.derocode.rag.splitters;

import com.derocode.rag.enums.DocumentType;
import com.derocode.rag.interfaces.DocumentSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfSplitter extends AbstractTokenDocumentSplitter {
    @Override
    public String getExtension() {
        return DocumentType.PDF.extension();
    }

    @Override
    public List<Document> split(List<Document> documents) {
        return split(
                documents,
                500,
                350,
                75
        );

    }


}

