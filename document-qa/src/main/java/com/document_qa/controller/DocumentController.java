package com.document_qa.controller;

import com.document_qa.service.DocumentService;
import com.document_qa.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final QAService qaService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Session-Id") String sessionId) {
        try {
            String documentId =
                    documentService.processDocument(file, sessionId);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Uploaded successfully! ID: " + documentId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<String> ask(
            @RequestBody Map<String, String> body,
            @RequestHeader("X-Session-Id") String sessionId) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(qaService.answer(
                        body.get("question"),
                        sessionId,
                        body.get("documentId")
                ));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Map<String, Object>>> documents(
            @RequestHeader("X-Session-Id") String sessionId) {
        return ResponseEntity.ok(
                documentService.getDocuments(sessionId));
    }

    @DeleteMapping("/session")
    public ResponseEntity<String> clearSession(
            @RequestHeader("X-Session-Id") String sessionId) {
        documentService.deleteSession(sessionId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Session cleared.");
    }
}