# Spring AI RAG and Local Model Testing Platform

**This also severs as the front end to the ECOMM application**

A *Spring AI RAG application and AI model experimentation platform* that combines vector search, document ingestion, local/remote LLMs, and **MCP tool integration**.

The project is designed to experiment with different models and deployment configurations while keeping the core application architecture unchanged.

## Features

* Spring AI RAG pipeline
* Qdrant vector database
* PostgreSQL metadata storage
* PDF, Markdown, and text document ingestion
* Configurable document chunking and embeddings
* Local Ollama models
* Remote Llama.cpp models
* MCP server and tool integration
* Tool-based application data retrieval
* Standard and streaming chat
* Docker Compose support
* No required cloud AI services

## Architecture

                    ┌──────────────────┐
                    │    Web Client    │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │   Spring AI App  │
                    └───────┬───┬──────┘
                            │   │
              ┌─────────────┘   └─────────────┐
              ▼                               ▼
        ┌───────────┐                   ┌───────────┐
        │   Qdrant  │                   │ MCP Server│
        │   Vector  │                   │   Tools   │
        │   Search  │                   └─────┬─────┘
        └───────────┘                         │
                                             ▼
                                      ┌───────────────┐
                                      │ E-commerce    │
                                      │ Application   │
                                      └───────────────┘
                                             │
                                             ▼
                                      ┌───────────────┐
                                      │ Customer /    │
                                      │ Product Data  │
                                      └───────────────┘

                    ┌──────────────────┐
                    │  Ollama /        │
                    │  Llama.cpp       │
                    │  Chat Models     │
                    └──────────────────┘

## RAG

Documents are ingested, split into chunks, embedded, and stored in Qdrant.

```text
Documents → Chunking → Embeddings → Qdrant
                                      ↓
User Question → Semantic Search → Retrieved Context → LLM
```

RAG is used for retrieving knowledge from the application's document collection.

## MCP Tools

The application uses an **MCP server** to expose application functionality to the language model.

Tools can be used for tasks such as:

* Customer lookup
* Product lookup
* Order lookup
* Document retrieval
* Date/time operations
* Weather
* Other application-specific operations

This allows the model to use **RAG for knowledge retrieval** and **MCP tools for application data and operations**.

## Model Options

The application can be configured for different model environments.

### Local

* Ollama
* `llama3.2`
* `nomic-embed-text`
* MCP server

### Remote

* Llama.cpp
* `Llama-3.1-8B-Instruct`
* `nomic-embed-text`
* MCP server

The architecture can also be used with other Spring AI supported model providers.

## Chat Interfaces

### Standard Chat

```text
http://localhost:8080/rag
```

API:

```text
POST /rag/call
```

Used for testing standard model responses, RAG, MCP tools, and tool calling.

### Streaming Chat

```text
http://localhost:8080/rag/streaming
```

API:

```text
POST /rag/stream
```

Used for testing streaming responses, latency, RAG, MCP tools, and different model configurations.

## Endpoints

| Endpoint         | Method | Purpose            |
| ---------------- | ------ | ------------------ |
| `/rag`           | GET    | Standard chat UI   |
| `/rag/streaming` | GET    | Streaming chat UI  |
| `/rag/call`      | POST   | Standard response  |
| `/rag/stream`    | POST   | Streaming response |
| `/rag/ingest`    | POST   | Ingest documents   |

## Running

### 1. Start infrastructure

Start:

* PostgreSQL
* Qdrant
* Ollama or remote Llama.cpp
* MCP server

Docker Compose can be used for the required infrastructure.

### 2. Select a profile

Local example:

```properties
spring.profiles.active=home
```

Remote example:

```properties
spring.profiles.active=remote
```

### 3. Ingest documents

Place `.pdf`, `.md`, or `.txt` files in the configured document location and run:

```text
POST http://localhost:8080/rag/ingest
```

### 4. Start chatting

Standard:

```text
http://localhost:8080/rag
```

Streaming:

```text
http://localhost:8080/rag/streaming
```

## Project Goal

This project is a practical environment for experimenting with:

* **Spring AI**
* **RAG**
* **Vector search**
* **MCP and tool calling**
* **Local and remote LLMs**
* **Embeddings**
* **Ollama**
* **Llama.cpp**
* **Streaming AI responses**

The goal is to provide a flexible platform for testing different models, tools, and deployment configurations without redesigning the application architecture.
