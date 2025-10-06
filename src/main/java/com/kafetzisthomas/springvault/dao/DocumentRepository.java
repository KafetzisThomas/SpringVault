package com.kafetzisthomas.springvault.dao;

import com.kafetzisthomas.springvault.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    // retrieve a list of documents associated with the specified owner's username
    List<DocumentSummary> findAllByOwnerUsername(String ownerUsername);

    // retrieve a single document by id and owner's username
    Optional<Document> findByIdAndOwnerUsername(UUID id, String ownerUsername);

}
