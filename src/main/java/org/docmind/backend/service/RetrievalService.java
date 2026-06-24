package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    public List<Document> retrieveRelevantChunks(String question) {

        SearchRequest searchRequest = SearchRequest.builder()
                        .query(question)
                        .topK(7)
                        .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}