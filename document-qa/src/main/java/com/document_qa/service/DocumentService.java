package com.document_qa.service;

import com.document_qa.model.DocumentChunk;
import com.document_qa.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentChunkRepository chunkRepository;

    public String processDocument(
            MultipartFile file, String sessionId) throws Exception {

        String documentId = UUID.randomUUID().toString();
        String documentName = file.getOriginalFilename();

        // Delete old version of same doc for this session only
        chunkRepository.deleteByDocumentNameAndSessionId(
                documentName, sessionId);

        String text = extractText(file);
        String cleanedText = cleanText(text);
        List<String> chunks = chunkText(cleanedText, 500);

        List<DocumentChunk> entities = chunks.stream()
                .filter(c -> !c.isBlank())
                .map(chunk -> {
                    DocumentChunk dc = new DocumentChunk();
                    dc.setDocumentId(documentId);
                    dc.setDocumentName(documentName);
                    dc.setSessionId(sessionId);   // ← tag with session
                    dc.setContent(chunk);
                    return dc;
                }).toList();

        chunkRepository.saveAll(entities);
        log.info("Saved {} chunks for session: {} doc: {}",
                entities.size(), sessionId, documentName);

        return documentId;
    }

    public List<java.util.Map<String, Object>> getDocuments(
            String sessionId) {
        return chunkRepository
                .findDocumentsBySession(sessionId)
                .stream()
                .map(row -> {
                    java.util.Map<String, Object> doc =
                            new java.util.LinkedHashMap<>();
                    doc.put("documentId", row[0]);
                    doc.put("documentName", row[1]);
                    doc.put("chunkCount", row[2]);
                    doc.put("uploadedAt", row[3]);
                    return doc;
                }).toList();
    }

    public void deleteSession(String sessionId) {
        chunkRepository.deleteBySessionId(sessionId);
    }

    private String extractText(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null)
            throw new IllegalArgumentException("Invalid file name");

        try (InputStream is = file.getInputStream()) {
            if (filename.endsWith(".pdf")) {
                try (PDDocument pdf = Loader.loadPDF(file.getBytes())) {
                    return new PDFTextStripper().getText(pdf);
                }
            }
            if (filename.endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(is)) {
                    StringBuilder sb = new StringBuilder();
                    doc.getParagraphs()
                            .forEach(p -> sb.append(p.getText()).append("\n"));
                    return sb.toString();
                }
            }
            return new String(file.getBytes());
        }
    }

    private String cleanText(String text) {
        if (text == null) return "";
        text = text
                .replace("\u00fb", "u")
                .replace("\u00e6", "ae")
                .replace("\u2019", "'")
                .replace("\u2018", "'")
                .replace("\u201c", "\"")
                .replace("\u201d", "\"")
                .replace("\u2013", "-")
                .replace("\u2014", "--")
                .replace("\u2022", "*")
                .replace("\u2192", "->")
                .replace("\u00a0", " ");
        text = text.replaceAll("[^\\x20-\\x7E\\n\\r\\t]", " ");
        text = text.replaceAll("[ \\t]+", " ").trim();
        return text;
    }

    private List<String> chunkText(String text, int chunkSize) {
        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String word : words) {
            sb.append(word).append(" ");
            count++;
            if (count >= chunkSize) {
                chunks.add(sb.toString().trim());
                sb = new StringBuilder();
                count = 0;
            }
        }
        if (!sb.isEmpty()) chunks.add(sb.toString().trim());
        return chunks;
    }
}