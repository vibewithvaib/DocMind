package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.document.DocumentResponse;
import org.docmind.backend.entity.Document;
import org.docmind.backend.entity.User;
import org.docmind.backend.exception.DocumentNotFoundException;
import org.docmind.backend.exception.InvalidFileTypeException;
import org.docmind.backend.exception.UserNotFoundException;
import org.docmind.backend.repository.DocumentRepository;
import org.docmind.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PdfProcessingService pdfProcessingService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public DocumentResponse uploadDocument(MultipartFile file, String userEmail) throws IOException {

        validatePdf(file);

        User user = userRepository.findByEmail(userEmail).orElseThrow(
                                () -> new UserNotFoundException(
                                        "User not found"
                                )
                        );

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(
                    uploadPath
            );
        }

        String fileName = file.getOriginalFilename();

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document document = Document.builder()
                        .fileName(fileName)
                        .filePath(filePath.toString())
                        .uploadedBy(user)
                        .build();

        Document savedDocument = documentRepository.save(document);
        pdfProcessingService.processDocument(savedDocument);
        return mapToResponse(savedDocument);
    }

    public List<DocumentResponse> getAllDocuments() {

        List<Document> documents = documentRepository.findAll();

        return documents.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteDocument(Long documentId) throws IOException {

        Document document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(
                                () ->
                                        new DocumentNotFoundException(
                                                "Document not found"
                                        )
                        );

        Path filePath =
                Paths.get(
                        document.getFilePath()
                );

        Files.deleteIfExists(
                filePath
        );

        documentRepository.delete(
                document
        );
    }

    private void validatePdf(MultipartFile file) {

        if (file.isEmpty()) {

            throw new InvalidFileTypeException(
                    "File cannot be empty"
            );
        }

        if (!"application/pdf".equals(
                file.getContentType()
        )) {

            throw new InvalidFileTypeException(
                    "Only PDF files are allowed"
            );
        }
    }

    private DocumentResponse mapToResponse(
            Document document
    ) {

        return DocumentResponse
                .builder()
                .id(document.getId())
                .fileName(
                        document.getFileName()
                )
                .uploadedAt(
                        document.getUploadedAt()
                )
                .build();
    }
}