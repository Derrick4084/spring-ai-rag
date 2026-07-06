package com.derocode.rag.readers;

import com.derocode.rag.enums.DocumentType;
import com.derocode.rag.interfaces.DocumentReader;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class PdfDocumentReader implements DocumentReader {

    @Override
    public String getSupportedExtension() {
        return DocumentType.PDF.extension();
    }

    @Override
    public List<Document> read(Resource resource) {

        List<Document> documents = new PagePdfDocumentReader(resource).get();

        return documents.stream().map(
                document -> {
                    Map<String,Object> metaData = new HashMap<>(document.getMetadata());
                    metaData.put("type", DocumentType.PDF.extension());
                    metaData.put("file_name", resource.getFilename());
                    return Document.builder()
                            .text(cleanPdfText(document))
                            .metadata(metaData)
                            .build();
                }
        ).toList();

    }

    private String cleanPdfText(@NonNull Document doc) {

        return Objects.requireNonNull(doc.getText())
                .replace("\r", "")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
//        String text = doc.getText();
//        assert text != null;
//        text = text.replaceAll("\\r", "");
//        text = text.replaceAll("[ \\t]+", " ");
//        return  text;
    }
}
