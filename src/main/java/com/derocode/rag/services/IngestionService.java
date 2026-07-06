package com.derocode.rag.services;

import com.derocode.rag.configs.ResourceHashGenerator;
import com.derocode.rag.readers.DocumentReaderFactory;
import com.derocode.rag.splitters.DocumentSplitterFactory;
import com.derocode.rag.interfaces.DocumentReader;
import com.derocode.rag.interfaces.DocumentSplitter;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.slf4j.*;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final DocumentReaderFactory readerFactory;
    private final DocumentSplitterFactory splitterFactory;
    private final QdrantClient qdrantClient;
    private final QdrantIndexerService indexerService;

    public IngestionService(DocumentReaderFactory readerFactory, DocumentSplitterFactory splitterFactory, QdrantClient qdrantClient, QdrantIndexerService indexerService) {
        this.readerFactory = readerFactory;
        this.splitterFactory = splitterFactory;
        this.qdrantClient = qdrantClient;
        this.indexerService = indexerService;
    }

    public void loadAll() throws IOException {

        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] pdfs =
                resolver.getResources("classpath:documents/pdf/*.pdf");

        Resource[] markdown =
                resolver.getResources("classpath:documents/markdown/*.md");

        Resource[] text =
                resolver.getResources("classpath:documents/text/*.txt");

        List<Resource> resources = Stream.of(
                        pdfs,
                        markdown,
                        text
                ).flatMap(Arrays::stream)
                .toList();


        for(var resource: resources){

            String resourceHash = ResourceHashGenerator.generate(resource);

            DocumentReader reader = readerFactory.getReader(resource);
            DocumentSplitter splitter = splitterFactory.getSplitter(resource);

            List<Document> readDocuments = reader.read(resource);


            readDocuments.forEach(document -> {
                document.getMetadata().put("document_hash", resourceHash);
                try {
                    document.getMetadata().put("source", resource.getURI().toString());
                } catch (IOException e) {
                    log.info("Problem with uri for: {}", resource.getFilename());
                }
            });

            List<Document> chunks = splitter.split(readDocuments);

            if(chunks.isEmpty()){
                log.info("File {} could not be embedded.", resource.getFilename());
                continue;
            }

            for (int i = 0; i < chunks.size(); i++) {
                chunks.get(i).getMetadata().put("chunk_index", i);
                chunks.get(i).getMetadata().put("chunk_count", chunks.size());

            }

            List<Points.PointStruct> pointStructs = indexerService.toPoints(chunks);

            try {
                Points.UpdateResult pointsResult = qdrantClient.upsertAsync(
                        "derocode", pointStructs)
                        .get();
                log.info(pointsResult.toString());

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
