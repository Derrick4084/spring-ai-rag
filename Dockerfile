FROM ollama/ollama:latest AS builder

# Start Ollama server in the background and pull the model
RUN ollama serve & \
    until ollama list >/dev/null 2>&1; do sleep 1; done && \
    ollama pull llama3.2:latest && \
    ollama pull nomic-embed-text:latest

# Stage 2: Final clean production image
FROM ollama/ollama:latest

# Copy the pre-downloaded model cache from the builder stage
COPY --from=builder /root/.ollama /root/.ollama

# Expose the default Ollama API port
EXPOSE 11434

# Set the default entrypoint to run the Ollama server
ENTRYPOINT ["ollama", "serve"]