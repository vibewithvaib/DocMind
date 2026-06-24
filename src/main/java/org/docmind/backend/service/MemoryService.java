package org.docmind.backend.service;

import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.ChatMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveMessage(
            String email,
            ChatMessage message
    ) {

        String key =
                "chat:" + email;

        redisTemplate
                .opsForList()
                .rightPush(
                        key,
                        message
                );

        redisTemplate
                .opsForList()
                .trim(
                        key,
                        -10,
                        -1
                );

        redisTemplate.expire(
                key,
                Duration.ofHours(24)
        );
    }

    public List<Object> getConversation(
            String email
    ) {

        String key =
                "chat:" + email;

        return redisTemplate
                .opsForList()
                .range(
                        key,
                        0,
                        -1
                );
    }
}