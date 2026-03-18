package com.document_qa.controller;

import com.document_qa.service.DocumentService;
import com.document_qa.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final QAService qaService;

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody Map<String, String> body) {
        String answer = qaService.answer(body.get("question"));
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(answer);
    }

    // Upload endpoint
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String documentId = documentService.processDocument(file);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                    .body("Document uploaded and processed successfully! Document ID: " + documentId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                    .body("Error: " + e.getMessage());
        }
    }

}