package com.derocode.rag.services;

import com.derocode.rag.configs.ResourceHashGenerator;
import com.derocode.rag.entities.PointMetaData;
import com.derocode.rag.readers.DocumentReaderFactory;
import com.derocode.rag.repository.PointMetaDataRepository;
import com.derocode.rag.splitters.DocumentSplitterFactory;
import com.derocode.rag.interfaces.DocumentReader;
import com.derocode.rag.interfaces.DocumentSplitter;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common;
import io.qdrant.client.grpc.Common.*;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.Points.*;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.slf4j.*;

import static io.qdrant.client.ConditionFactory.matchKeyword;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final DocumentReaderFactory readerFactory;
    private final DocumentSplitterFactory splitterFactory;
    private final QdrantClient qdrantClient;
    private final QdrantIndexerService indexerService;
    private final PointMetaDataRepository pointMetaDataRepository;

    private final String collectionName;

    public IngestionService(
            DocumentReaderFactory readerFactory,
            DocumentSplitterFactory splitterFactory,
            QdrantClient qdrantClient,
            QdrantIndexerService indexerService,
            PointMetaDataRepository pointMetaDataRepository,
            @Value("${spring.ai.vectorstore.qdrant.collection-name}") String collectionName
    ) {
        this.readerFactory = readerFactory;
        this.splitterFactory = splitterFactory;
        this.qdrantClient = qdrantClient;
        this.indexerService = indexerService;
        this.pointMetaDataRepository = pointMetaDataRepository;
        this.collectionName = collectionName;
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


        for(var resource: resources) {


            String resourceHash = ResourceHashGenerator.generate(resource);
            String fileName = resource.getFilename();
            String filePath = resource.getFilePath().toString();

            Optional<PointMetaData> point = pointMetaDataRepository.findByFileNameAndFilePath(fileName, filePath);


            if (point.isPresent() && Objects.equals(point.get().getDocumentHash(), resourceHash)) {
                continue;
            }

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

            if (chunks.isEmpty()) {
                log.info("File {} could not be embedded.", resource.getFilename());
                continue;
            }

            for (int i = 0; i < chunks.size(); i++) {
                chunks.get(i).getMetadata().put("chunk_index", i);
                chunks.get(i).getMetadata().put("chunk_count", chunks.size());

            }


            List<Points.PointStruct> pointStructs = indexerService.toPoints(chunks);

            UpdateResult pointsResult;
            try {
                pointsResult = qdrantClient.upsertAsync(
                                UpsertPoints.newBuilder()
                                        .setCollectionName(collectionName)
                                        .addAllPoints(pointStructs)
                                        .setWait(true)
                                        .build())
                        .get();

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            if (pointsResult.getStatus() != UpdateStatus.Completed) {
                throw new IllegalStateException("Qdrant upsert failed");
            }


            if(point.isPresent()){
                PointMetaData pointMetaData = point.get();
                try {
                    qdrantClient.deleteAsync(
                                    collectionName,
                                    Filter.newBuilder()
                                            .addMust(matchKeyword("document_hash", pointMetaData.getDocumentHash()))
                                            .build())
                            .get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                pointMetaData.setDocumentHash(resourceHash);
                pointMetaData.setUpdatedAt(LocalDateTime.now());
                pointMetaDataRepository.save(pointMetaData);
            } else {
                PointMetaData data = PointMetaData.builder()
                        .documentHash(resourceHash)
                        .fileName(resource.getFilename())
                        .filePath(resource.getFilePath().toString())
                        .sourceUri(resource.getURI().toString())
                        .createDate(LocalDateTime.now())
                        .build();

                pointMetaDataRepository.save(data);

            }
         }
    }

    public void removeDocument(@NonNull Document document) {

        String hash = document.getMetadata().get("document_hash").toString();
        String fileName = document.getMetadata().get("file_name").toString();


        try {
            qdrantClient.deleteAsync(
                    collectionName,
                    Common.Filter.newBuilder()
                            .addMust(matchKeyword("document_hash", hash))
                            .build()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }




    }
}
