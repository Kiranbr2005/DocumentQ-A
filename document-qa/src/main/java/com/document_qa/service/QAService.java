package com.document_qa.service;

import com.document_qa.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QAService {

    private final ChatModel chatModel;
    private final DocumentChunkRepository chunkRepository;

    public String answer(String question) {

        // 1. Try full-text search first
        List<String> chunks = chunkRepository.findByFullTextSearch(question, 5);

        // 2. If no results found → fallback to ALL chunks
        if (chunks.isEmpty()) {
            chunks = chunkRepository.findAllChunks(5);
        }

        // 3. If still empty → no document uploaded at all
        if (chunks.isEmpty()) {
            return "No documents found. Please upload a document first.";
        }

        // 4. Build prompt and call Groq
        String context = String.join("\n\n", chunks);
        String prompt = """
                You are a helpful assistant. Answer the question using only the context below.
                If the answer is not in the context, say "I don't know based on the provided document."

                Context:
                %s

                Question: %s
                """.formatted(context, question);

        return chatModel.call(prompt);
    }
}