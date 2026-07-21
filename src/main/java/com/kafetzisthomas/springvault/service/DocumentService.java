package com.kafetzisthomas.springvault.service;

import com.kafetzisthomas.springvault.entity.Document;
import com.kafetzisthomas.springvault.repository.DocumentSummary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DocumentService {

    List<DocumentSummary> getAllDocuments(String username);

    Document getDocumentById(UUID id, String username);

    void addDocument(MultipartFile file, String username) throws IOException;

    void deleteDocument(UUID id, String username);

}
