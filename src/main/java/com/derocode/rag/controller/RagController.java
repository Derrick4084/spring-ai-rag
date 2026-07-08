package com.derocode.rag.controller;


import com.derocode.rag.records.RagPrompt;
import com.derocode.rag.services.IngestionService;
import com.derocode.rag.services.ChatService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("rag")
public class RagController {

    private final ChatService chatService;
    private final IngestionService readerService;

    public RagController(ChatService chatService, IngestionService readerService) {
        this.chatService = chatService;
        this.readerService = readerService;
    }

    private String generateId(){
        return UUID.randomUUID().toString();
    }

    @PostMapping(
            value = "/chat",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> chat(@RequestBody @NonNull RagPrompt dto) {
        return chatService.send(dto.prompt(), generateId());
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocuments() {

        try {
            readerService.loadAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body("Loaded successfully");

    }

}
