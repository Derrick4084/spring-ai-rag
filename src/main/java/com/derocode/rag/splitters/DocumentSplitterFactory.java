package com.derocode.rag.splitters;


import com.derocode.rag.interfaces.DocumentSplitter;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocumentSplitterFactory {


    private final Map<String, DocumentSplitter> splitters;


    public DocumentSplitterFactory(@NonNull List<DocumentSplitter> splitters) {
        this.splitters = splitters.stream()
                .collect(Collectors.toMap(
                                DocumentSplitter::getExtension,
                                Function.identity()
                ));
    }


    public DocumentSplitter getSplitter(@NonNull Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Resource has no filename");
        }

        int index = filename.lastIndexOf('.');
        if (index == -1) {
            throw new IllegalArgumentException(
                    "No file extension found for " + filename);
        }

        String extension = filename.substring(index + 1).toLowerCase();
        DocumentSplitter splitter = splitters.get(extension);
        if(splitter==null) {
            throw new IllegalArgumentException("Unsupported splitter");
        }

        return splitter;


    }
}
