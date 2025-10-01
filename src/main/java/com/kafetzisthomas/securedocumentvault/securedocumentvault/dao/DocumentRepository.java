package com.kafetzisthomas.securedocumentvault.securedocumentvault.dao;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    // retrieve a list of documents associated with the specified owner's username
    List<DocumentSummary> findAllByOwnerUsername(String ownerUsername);

}
