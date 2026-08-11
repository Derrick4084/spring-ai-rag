package com.derocode.rag.splitters;

import com.derocode.rag.interfaces.DocumentSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.List;

public abstract class AbstractTokenDocumentSplitter implements DocumentSplitter {

    protected List<Document> split(
            List<Document> documents,
            int chunkSize,
            int minChunkChars,
            int minChunkLength
    ){

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(chunkSize)
                .withMinChunkSizeChars(minChunkChars)
                .withMinChunkLengthToEmbed(minChunkLength)
                .withKeepSeparator(true)
                .withMaxNumChunks(Integer.MAX_VALUE)
                .build();

        return splitter.apply(documents);

    };
}
