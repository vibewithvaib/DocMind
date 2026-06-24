package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.ChatMessage;
import org.docmind.backend.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    private final RetrievalService retrievalService;

    private final MemoryService memoryService;

    public ChatResponse askQuestion(
            String question,
            String email
    ) {

        List<Object> history =
                memoryService.getConversation(
                        email
                );

        StringBuilder conversationHistory =
                new StringBuilder();

        for (Object object : history) {

            ChatMessage message =
                    (ChatMessage) object;

            conversationHistory
                    .append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }

        List<Document> documents =
                retrievalService
                        .retrieveRelevantChunks(
                                question
                        );

        StringBuilder context =
                new StringBuilder();

        for (Document document : documents) {

            context.append(
                    document.getText()
            );

            context.append("\n\n");
        }

        String prompt =
                """
                You are DocMind, an enterprise document assistant.
        
                Your task is to answer the user's question ONLY using the provided document context.
        
                Rules:
        
                1. Carefully analyze ALL retrieved context before answering.
        
                2. Do NOT make up information.
        
                3. Do NOT use external knowledge.
        
                4. If the answer is partially available, provide the available information and clearly mention what is missing.
        
                5. If the answer is not present in the context, respond exactly with:
                   "Information not found in uploaded documents."
        
                6. When multiple chunks contain related information, combine them into a single complete answer.
        
                7. Prefer factual statements from the documents over assumptions.
        
                8. Give detailed and professional answers rather than one-line responses.
        
                Previous Conversation:
        
                %s
        
                Document Context:
        
                %s
        
                User Question:
        
                %s
        
                Answer:
                """
                        .formatted(
                                conversationHistory,
                                context,
                                question
                        );
        memoryService.saveMessage(
                email,
                new ChatMessage(
                        "USER",
                        question
                )
        );

        String answer =
                chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();

        memoryService.saveMessage(
                email,
                new ChatMessage(
                        "AI",
                        answer
                )
        );

        return new ChatResponse(
                answer
        );
    }
}