package org.docmind.backend.controller;

import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.ChatRequest;
import org.docmind.backend.dto.ChatResponse;
import org.docmind.backend.service.ChatService;
import org.docmind.backend.service.MemoryService;
//import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemoryService memoryService;
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(
            @RequestBody ChatRequest request,
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        ChatResponse response =
                chatService.askQuestion(
                        request.getQuestion(),
                        email
                );

        return ResponseEntity.ok(
                response
        );
    }
    @GetMapping("/history")
    public ResponseEntity<List<Object>>
    getHistory(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                memoryService.getConversation(
                        email
                )
        );
    }

}