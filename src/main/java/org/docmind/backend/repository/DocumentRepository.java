package org.docmind.backend.repository;

import org.docmind.backend.entity.Document;
import org.docmind.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}