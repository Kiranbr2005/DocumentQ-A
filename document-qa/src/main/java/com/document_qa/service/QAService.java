package com.document_qa.service;

import com.document_qa.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QAService {

    private final ChatModel chatModel;
    private final DocumentChunkRepository chunkRepository;

    public String answer(
            String question,
            String sessionId,
            String documentId) {

        List<String> chunks;

        // If specific document selected → search only that doc
        if (documentId != null && !documentId.isBlank()) {
            chunks = chunkRepository
                    .findByDocumentAndSession(
                            documentId, sessionId, question, 5);
            log.info("Doc-scoped search: {} chunks for doc: {}",
                    chunks.size(), documentId);

            // Fallback within same document
            if (chunks.isEmpty()) {
                chunks = chunkRepository
                        .findLatestByDocument(documentId, sessionId, 5);
            }
        } else {
            // Search across all session docs
            chunks = chunkRepository
                    .findBySessionAndQuery(sessionId, question, 5);
            if (chunks.isEmpty()) {
                chunks = chunkRepository
                        .findLatestBySession(sessionId, 5);
            }
        }

        if (chunks.isEmpty()) {
            return "No documents found. Please upload a document first.";
        }

        String context = String.join("\n\n", chunks);
        String prompt = """
        You are a helpful assistant. Answer the question \
        using only the context below.
        If the answer is not in the context, say \
        "I don't know based on the provided document."

        Context:
        %s

        Question: %s
        """.formatted(context, question);

        return chatModel.call(prompt);
    }
}