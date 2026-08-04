# Spring AI RAG and Local Model Testing Platform

A Retrieval-Augmented Generation (RAG) application built with **Spring AI** that demonstrates how to build a local AI knowledge base using vector search, document ingestion, embeddings, and large language models.

The application is also designed as a **model experimentation and testing platform**. It allows you to configure and run different AI model configurations based on the compute resources available to your environment. You can experiment with local models, remote model servers, different model sizes, and different deployment approaches without changing the core RAG application architecture.

The application provides two chat interfaces so you can test both **standard request/response** and **streaming model responses**.

---

## Features

* Spring AI powered RAG pipeline
* Large language model integration
* Qdrant vector database for semantic search
* PostgreSQL for vector/document metadata
* Document ingestion and automatic indexing
* Support for PDF documents
* Support for Markdown (`.md`) files
* Support for plain text (`.txt`) files
* Configurable document readers and text splitters
* Local model execution
* Remote model execution
* Ollama support
* Llama.cpp support
* MCP (Model Context Protocol) server integration
* Standard model request/response testing
* Streaming model response testing
* Ability to experiment with different models based on available compute resources
* Docker Compose support for local infrastructure
* Designed to run without requiring cloud AI services

---

## Model and Deployment Options

The application is designed to allow you to experiment with different model configurations depending on the hardware and compute resources available.

### Option 1: Local Ollama

Run models locally using a containerized Ollama environment.

Example configuration:

* `llama3.2`
* `nomic-embed-text`
* MCP server (Model Context Protocol)

This configuration is useful for running the entire AI stack locally.

---

### Option 2: Remote Llama.cpp

Run the language model on a separate machine using Llama.cpp while the main Spring AI application runs locally or in another environment.

Example configuration:

* `Llama-3.1-8B-Instruct`
* `nomic-embed-text`
* MCP server (Model Context Protocol)

This configuration allows you to move model inference to a machine with greater CPU, GPU, or memory resources.

---

### Experimenting With Other Models

The architecture is intended to make model experimentation straightforward.

Depending on the compute resources available, you can experiment with:

* Smaller models for systems with limited resources
* Larger models for systems with more CPU/GPU memory
* Local Ollama models
* Remote Llama.cpp models
* Other Spring AI supported model providers

The goal is to allow the same RAG application to be used as a testing environment for different model configurations.

---

## RAG Pipeline

The application implements a Retrieval-Augmented Generation workflow.

1. Documents are read and processed.
2. Document content is split into chunks optimized for retrieval.
3. Chunks are converted into embeddings using `nomic-embed-text`.
4. Embeddings are stored in Qdrant.
5. Vector/document metadata is stored in PostgreSQL for tracking and idempotency.
6. A user's question is processed and used to search the vector database.
7. The most relevant document chunks are retrieved.
8. Retrieved context is supplied to the configured language model.
9. The language model generates a context-aware response.

The RAG architecture allows the language model to answer questions using information retrieved from your own document collection.

---

## Supported Documents

The application currently supports automatic ingestion of:

* PDF documents
* Markdown (`.md`) files
* Plain text (`.txt`) files

The reader and splitter architecture is designed to be extendable, allowing additional document types to be added as the application evolves.

---

## Chat Interfaces

The application provides two separate web interfaces for testing model behavior.

### Standard Chat

Navigate to:

`http://localhost:8080/rag`

This interface uses the standard request/response model flow.

It is useful for testing:

* Standard model responses
* RAG responses
* Tool calls
* MCP integration
* Response formatting
* Overall model behavior

The underlying API endpoint is:

`POST /rag/call`

---

### Streaming Chat

Navigate to:

`http://localhost:8080/rag/streaming`

This interface is designed specifically for testing streaming model responses.

It allows you to evaluate:

* Token-by-token response streaming
* Streaming latency
* Time to first response
* Long-running model responses
* Streaming behavior with RAG
* Streaming behavior when using different model configurations

The underlying API endpoint is:

`POST /rag/stream`

The streaming interface is especially useful when experimenting with different models and deployment configurations because you can compare how quickly different models begin producing output and how they behave during longer responses.

---

## Application Endpoints

| Endpoint         | Method | Purpose                         |
| ---------------- | ------ | ------------------------------- |
| `/rag`           | `GET`  | Standard chat interface         |
| `/rag/streaming` | `GET`  | Streaming chat interface        |
| `/rag/call`      | `POST` | Standard model request/response |
| `/rag/stream`    | `POST` | Streaming model response        |
| `/rag/ingest`    | `POST` | Ingest and index documents      |

---

## Running the Application

### 1. Start the AI Model Environment

Choose the model configuration that best fits your available compute resources.

For example, you can run:

* Ollama locally
* Llama.cpp remotely
* Another supported model configuration

The application can be configured to use different model environments through Spring configuration and profiles.

---

### 2. Start PostgreSQL and Qdrant

The application requires:

* PostgreSQL
* Qdrant

You can use the provided Docker Compose configuration to start the required infrastructure.

---

### 3. Configure Your Documents

Add your own documents to the configured document locations.

Supported formats include:

* `.pdf`
* `.txt`
* `.md`

Configure the appropriate classpaths or document locations according to your application configuration.

---

### 4. Ingest Your Documents

The application provides an ingestion endpoint that loads your documents, processes them, generates embeddings, and stores them in Qdrant.

You can call the endpoint using Postman, curl, or another HTTP client.

```text
POST http://localhost:8080/rag/ingest
```

---

### 5. Configure Database Schema Management

For the initial application run, use:

```properties
spring.jpa.hibernate.ddl-auto=create
```

After the initial database schema has been created, change the configuration to:

```properties
spring.jpa.hibernate.ddl-auto=none
```

This prevents the existing vector/document metadata from being recreated on subsequent application starts.

---

## Model Profiles

The application supports different runtime configurations through Spring profiles.

### Remote Model Configuration

For a remote Llama.cpp and MCP server configuration, use the appropriate `remote` profile.

```text
spring.profiles.active=remote
```

### Local Model Configuration

For a local Ollama and embedding model configuration, use the appropriate local/home profile configured by the application.

```text
spring.profiles.active=home
```

The exact model and server configuration can be adjusted in the corresponding Spring application configuration files.

---

## Testing the Application

Once the application and supporting services are running, you can test the two model response modes.

### Standard Response Testing

Open:

```text
http://localhost:8080/rag
```

Use this interface to test standard request/response behavior.

### Streaming Response Testing

Open:

```text
http://localhost:8080/rag/streaming
```

Use this interface to test streaming behavior.

These two interfaces allow you to compare how different models perform when using standard responses versus streaming responses.

---

## Extending the Application

The application uses a reader and splitter architecture, making it easy to support additional document types.

Additional document readers and splitters can be implemented as the application evolves.

The model configuration can also be extended to support additional model providers and deployment architectures.

---

## Using Other LLMs

Although the project currently demonstrates local model execution using Ollama and Llama.cpp, **Spring AI provides a consistent abstraction layer** that makes it possible to experiment with other model providers.

Potential integrations include:

* OpenAI
* Anthropic
* Google Gemini
* Azure OpenAI
* Amazon Bedrock
* Other Spring AI supported model providers

The model provider and model configuration can be changed without fundamentally changing the RAG pipeline.

This makes the application useful not only as a RAG implementation, but also as a **model experimentation platform** for evaluating different models, model sizes, deployment environments, and response strategies.

---

## Project Goal

The goal of this project is to provide a flexible Spring AI application that combines:

* Retrieval-Augmented Generation
* Vector search
* Document ingestion
* Local AI models
* Remote AI models
* MCP tool integration
* Standard model responses
* Streaming model responses
* Configurable model deployments

The application is intended to evolve as a practical environment for learning, experimenting with, and evaluating AI models using the compute resources available to you.
