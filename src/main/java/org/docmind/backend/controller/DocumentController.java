package org.docmind.backend.controller;

import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.document.DocumentResponse;
import org.docmind.backend.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {
        String userEmail =
                authentication.getName();

        DocumentResponse response =
                documentService.uploadDocument(
                        file,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {

        List<DocumentResponse> documents =
                documentService.getAllDocuments();

        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long id
    ) throws IOException {

        documentService.deleteDocument(id);

        return ResponseEntity.ok(
                "Document deleted successfully"
        );
    }
}