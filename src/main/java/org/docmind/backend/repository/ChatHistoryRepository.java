package org.docmind.backend.repository;

import org.docmind.backend.entity.ChatHistory;
import org.docmind.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByUser(User user);
}