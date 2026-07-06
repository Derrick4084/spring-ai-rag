package com.derocode.rag.readers;

import com.derocode.rag.enums.DocumentType;
import com.derocode.rag.interfaces.DocumentReader;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class MarkDownReader implements DocumentReader {

    @Override
    public String getSupportedExtension() {
        return DocumentType.MARKDOWN.extension();
    }

    @Override
    public List<Document> read(@NonNull Resource resource) {

        List<Resource> resources = new ArrayList<>();
        resources.add(resource);

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resources, MarkdownDocumentReaderConfig.defaultConfig());

        return reader.get().stream().map(
                document -> {
                    Map<String,Object> metaData = new HashMap<>(document.getMetadata());
                    metaData.put("type", DocumentType.MARKDOWN.extension());
                    metaData.put("file_name", resource.getFilename());
                    return Document.builder()
                            .text(document.getText())
                            .metadata(metaData)
                            .build();
                }
        ).toList();
     }
    }


