package com.kafetzisthomas.securedocumentvault.securedocumentvault.service;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.DocumentSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    // return documents only for the specified username
    List<DocumentSummary> getAllDocuments(String username);

    void addDocument(MultipartFile file) throws IOException;

}
