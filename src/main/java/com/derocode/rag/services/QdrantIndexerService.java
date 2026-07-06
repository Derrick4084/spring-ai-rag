package com.derocode.rag.services;


import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Common.*;
import io.qdrant.client.grpc.JsonWithInt.Value;
import io.qdrant.client.grpc.Points.*;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.qdrant.client.ValueFactory.value;


@Service
public class QdrantIndexerService {

    private static final Logger log = LoggerFactory.getLogger(QdrantIndexerService.class);
    private final EmbeddingModel embeddingModel;

    public QdrantIndexerService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<PointStruct> toPoints(@NonNull List<Document> chunks) {

        List<PointStruct> pointStructs = new ArrayList<>();

        List<String> texts = chunks.stream()
                .map(Document::getText)
                .toList();

        List<float[]> embeddings = embeddingModel.embed(texts);

        for (int i = 0; i < chunks.size(); i++) {

            Document chunk = chunks.get(i);
            float[] embedding = embeddings.get(i);

            String documentHash = (String) chunk.getMetadata().get("document_hash");
            int chunkIndex = (Integer) chunk.getMetadata().get("chunk_index");
            String pointHashData = documentHash + "-" + chunkIndex;

            UUID pointUuid = UUID.nameUUIDFromBytes(pointHashData.getBytes(StandardCharsets.UTF_8));

            PointId pointId = PointId.newBuilder()
                    .setUuid(pointUuid.toString())
                    .build();

            List<Float> vector = new ArrayList<>(embedding.length);
            for(float value: embedding) {
                vector.add(value);
            }

            Map<String, Value> payload = new HashMap<>();

            chunk.getMetadata().forEach((key, value) ->
                    payload.put(key, toValue(value)));

            String text = Objects.requireNonNull(
                    chunk.getText(),
                    "Chunk text cannot be null."
            );

            payload.put("doc_content", value(text));

            PointStruct point = PointStruct.newBuilder()
                    .setId(pointId)
                    .setVectors(
                            VectorsFactory.vectors(vector)
                    )
                    .putAllPayload(payload)
                    .build();

            pointStructs.add(point);

        }
        return pointStructs;
    }


    private @NonNull Value toValue(Object value) {
        if (value instanceof String s) {
            return value(s);
        }
        if (value instanceof Integer i) {
            return value(i);
        }
        if (value instanceof Long l) {
            return value(l);
        }
        if (value instanceof Boolean b) {
            return value(b);
        }
        if (value instanceof Double d) {
            return value(d);
        }
        return value(value.toString());
    }

}
