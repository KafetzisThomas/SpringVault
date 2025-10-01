package com.kafetzisthomas.securedocumentvault.securedocumentvault.service;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.DocumentRepository;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.DocumentSummary;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.Document;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService{

    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<DocumentSummary> getAllDocuments(String username) {
        return documentRepository.findAllByOwnerUsername(username);
    }

    @Override
    public void addDocument(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        // sanitize filename before storing
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

        Document entity = new Document();
        entity.setId(UUID.randomUUID());
        entity.setFilename(filename);
        entity.setContentType(file.getContentType());
        entity.setData(file.getBytes());
        entity.setUploadedAt(Instant.now());

        // set owner username (current authenticated user)
        entity.setOwnerUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        documentRepository.save(entity);
    }

}
