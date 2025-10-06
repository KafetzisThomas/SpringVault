package com.kafetzisthomas.springvault.service;

import com.kafetzisthomas.springvault.dao.DocumentSummary;
import com.kafetzisthomas.springvault.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DocumentService {

    // return documents only for the specified username
    List<DocumentSummary> getAllDocuments(String username);

    // return a single document by id if owned by the specified username, otherwise throw 404
    Document getDocumentById(UUID id, String username);

    // upload a document for the specified username
    void addDocument(MultipartFile file, String username) throws IOException;

}
