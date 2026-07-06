package com.derocode.rag.readers;



import com.derocode.rag.interfaces.DocumentReader;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class DocumentReaderFactory {

    private final Map<String, DocumentReader> readers;

    public DocumentReaderFactory(@NonNull List<DocumentReader> readers) {
        this.readers = readers.stream()
                .collect(Collectors.toMap(
                        DocumentReader::getSupportedExtension,
                        Function.identity()
                ));
    }


    public DocumentReader getReader(@NonNull Resource resource){

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
        DocumentReader reader = readers.get(extension);
        if (reader == null) {
            throw new IllegalArgumentException(
                    "Unsupported file extension: " + extension);
        }

        return reader;
    }
}
