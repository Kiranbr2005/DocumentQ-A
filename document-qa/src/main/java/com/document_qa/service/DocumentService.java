package com.document_qa.service;

import com.document_qa.model.DocumentChunk;
import com.document_qa.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    @Autowired
    private final DocumentChunkRepository chunkRepository;

    // Main document processing method
    public String processDocument(MultipartFile file) throws Exception {

        // Generate a unique ID for this document
        String documentId = UUID.randomUUID().toString();

        // If you want to **replace old chunks for the same file name**, uncomment below:
//         chunkRepository.deleteByDocumentName(file.getOriginalFilename());

        String text = extractText(file);

        List<String> chunks = chunkText(text, 500);

        List<DocumentChunk> entities = chunks.stream().map(chunk -> {
            DocumentChunk dc = new DocumentChunk();
            dc.setDocumentId(documentId);
            dc.setDocumentName(file.getOriginalFilename());
            dc.setContent(chunk);
            return dc;
        }).toList();

        chunkRepository.saveAll(entities);

        return documentId;
    }
    // =========================
    // TEXT EXTRACTION
    // =========================
    private String extractText(MultipartFile file) throws Exception {

        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try (InputStream is = file.getInputStream()) {

            // 📄 PDF
            if (filename.endsWith(".pdf")) {
                try (PDDocument pdf = Loader.loadPDF(file.getBytes())) {
                    return new PDFTextStripper().getText(pdf);
                }
            }

            // 📘 DOCX
            if (filename.endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(is)) {
                    StringBuilder text = new StringBuilder();
                    doc.getParagraphs()
                            .forEach(p -> text.append(p.getText()).append("\n"));
                    return text.toString();
                }
            }

            // 📄 TXT / fallback
            return new String(file.getBytes());
        }
    }

    // =========================
    // SIMPLE CHUNKING (NO OVERLAP)
    // =========================
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

        if (!sb.isEmpty()) {
            chunks.add(sb.toString().trim());
        }

        return chunks;
    }
}