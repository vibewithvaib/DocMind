package org.docmind.backend.repository;

import org.docmind.backend.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {
}
