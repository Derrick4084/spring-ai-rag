package com.derocode.rag.readers;

import com.derocode.rag.enums.DocumentType;
import com.derocode.rag.interfaces.DocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class TextDocumentReader implements DocumentReader {


    @Override
    public String getSupportedExtension() {
        return DocumentType.TEXT.extension();
    }

    @Override
    public List<Document> read(Resource resource) {
        List<Document> documents = new TextReader(resource).read();

        return documents.stream().map(
                document -> {
                    Map<String,Object> metaData = new HashMap<>(document.getMetadata());
                    metaData.put("type",DocumentType.TEXT.extension());
                    metaData.put("file_name", resource.getFilename());
                    return Document.builder()
                            .text(document.getText())
                            .metadata(metaData)
                            .build();

                }
        ).toList();

    }
}
