package com.kafetzisthomas.springvault.repository;

import com.kafetzisthomas.springvault.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    // retrieve a list of documents associated with the specified owner's username and sort by newest first
    List<DocumentSummary> findAllByOwnerUsernameOrderByUploadedAtDesc(String ownerUsername);

    // retrieve a single document by id and owner's username
    Optional<Document> findByIdAndOwnerUsername(UUID id, String ownerUsername);

    // return 1 if record deleted otherwise 0 if failed
    long deleteByIdAndOwnerUsername(UUID id, String ownerUsername);
}
