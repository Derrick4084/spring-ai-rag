# Spring AI RAG Demo

A Retrieval-Augmented Generation (RAG) application built with **Spring AI** that demonstrates how to create a local AI knowledge base using vector search and large language models.

## Features

* Spring AI powered RAG pipeline
* Qdrant vector database for semantic search
* Containerized Ollama running locally with:

    * `llama3.2`
    * `nomic-embed-text`
* Automatic ingestion of:

    * PDF documents
    * Markdown (`.md`) files
    * Plain text (`.txt`) files
* Easily extendable to support additional document types
* Docker Compose for local development with no cloud dependencies

## How It Works

1. Documents are read and processed.
2. Content is split into chunks optimized for retrieval.
3. Chunks are converted into embeddings using `nomic-embed-text`.
4. Embeddings are stored in Qdrant.
5. User questions are embedded and matched against the vector database.
6. The most relevant document chunks are supplied to `llama3.2` to generate an accurate, context-aware response.

## Running the Application

1. Run the Dockerfile which creates the image with `llama3.2` and `nomic-embed-text`. About a 7gig image.
2. Add you own pdf, txt or .md files into folders choose your own classpaths and configure accordingly.
3. Start the application and Spring will start `Ollama` and `Qdrant` via the compose.yaml file.
4. There is an ingest endpoint, you can use Postman, curl which loads your files into `Qdrant`.

## Extending the Application

The application uses a reader and splitter architecture, making it easy to support additional document types by implementing custom document readers and splitters.

## Using Other LLMs

Although this project uses a local `Ollama` instance with `llama3.2` and `nomic-embed-text`, Spring AI provides a consistent abstraction layer, making it straightforward to switch to other providers such as:

* OpenAI
* Anthropic
* Google Gemini
* Azure OpenAI
* Amazon Bedrock
* Other Spring AI supported models

Simply update the Spring AI configuration to point to your preferred model provider.
