package com.kafetzisthomas.springvault.repository;

import com.kafetzisthomas.springvault.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<DocumentSummary> findAllByOwnerUsernameOrderByUploadedAtDesc(String ownerUsername);

    Optional<Document> findByIdAndOwnerUsername(UUID id, String ownerUsername);

    long deleteByIdAndOwnerUsername(UUID id, String ownerUsername);

}
