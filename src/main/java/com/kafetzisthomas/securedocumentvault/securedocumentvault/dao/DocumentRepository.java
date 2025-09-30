package com.kafetzisthomas.securedocumentvault.securedocumentvault.dao;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

}
