package com.derocode.rag.controller;


import com.derocode.rag.records.RagPrompt;
import com.derocode.rag.services.IngestionService;
import com.derocode.rag.services.ChatService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final ChatService chatService;
    private final IngestionService readerService;

    public RagController(
            ChatService chatService,
            IngestionService readerService) {

        this.chatService = chatService;
        this.readerService = readerService;
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Chat Call UI
     * GET <a href="http://localhost:8080/rag">...</a>
     */
    @GetMapping
    public ResponseEntity<Void> index() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/non-streaming.html"))
                .build();
    }

    /**
     * Streaming Chat UI
     * GET <a href="http://localhost:8080/rag/streaming">...</a>
     */
    @GetMapping("/streaming")
    public ResponseEntity<Void> streaming() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/streaming.html"))
                .build();
    }

    /**
     * Streaming Chat API
     * POST <a href="http://localhost:8080/rag/stream">...</a>
     */
    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> chatStream(
            @RequestBody @NonNull RagPrompt dto,
            @RequestHeader(
                    value = "X-Timezone",
                    defaultValue = "UTC"
            ) String timezone) {

        return chatService.sendStream(
                dto.prompt(),
                generateId(),
                timezone
        );
    }

    /**
     * Document Ingestion API
     * POST <a href="http://localhost:8080/rag/ingest">...</a>
     */
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocuments() {

        try {
            readerService.loadAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("Loaded successfully");
    }

    /**
     * Non-Streaming Chat API
     * POST <a href="http://localhost:8080/rag/call">...</a>
     */
    @PostMapping("/call")
    public String chatCall(
            @RequestBody @NonNull RagPrompt dto,
            @RequestHeader(
                    value = "X-Timezone",
                    defaultValue = "UTC"
            ) String timezone) {

        return chatService.sendCall(
                dto.prompt(),
                generateId(),
                timezone
        );
    }
}
