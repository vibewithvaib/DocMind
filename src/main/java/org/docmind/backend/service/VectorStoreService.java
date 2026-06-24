package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;
    public void storeChunk(Long chunkId, Long documentId, String fileName, String chunkText) {

        Document document = new Document(chunkText, Map.of(
                "chunkId", chunkId,
                "documentId", documentId,
                "fileName", fileName)
                );
        vectorStore.add(
                List.of(document)
        );
    }
}