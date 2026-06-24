package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.docmind.backend.entity.Document;
import org.docmind.backend.entity.DocumentChunk;
import org.docmind.backend.repository.DocumentChunkRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfProcessingService {

    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public void processDocument(Document document) throws IOException {
        String extractedText = extractText(document.getFilePath());
        List<String> chunks = chunkText(extractedText);
        saveChunks(document, chunks);
    }

    private String extractText(String pdfPath) throws IOException {

        File file = new File(pdfPath);
        try (PDDocument pdfDocument = Loader.loadPDF(file)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            return pdfTextStripper.getText(pdfDocument);
        }
    }

    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();

        int chunkSize = 700;
        int overlap = 150;

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            if (end == text.length()) {
                break;
            }
            start = end - overlap;
        }
        return chunks;
    }

    private void saveChunks(Document document, List<String> chunks) {

        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk documentChunk = DocumentChunk.builder()
                    .chunkText(chunks.get(i))
                            .chunkIndex(i)
                            .document(document)
                            .build();
            documentChunkRepository.save(documentChunk);
            vectorStoreService.storeChunk(
                    documentChunk.getId(),
                    document.getId(),
                    document.getFileName(),
                    documentChunk.getChunkText()
            );
        }
    }
}