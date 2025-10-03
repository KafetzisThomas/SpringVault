package com.kafetzisthomas.securedocumentvault.securedocumentvault.service;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.DocumentRepository;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.DocumentSummary;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.Document;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.security.AesGcmEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService{

    private final DocumentRepository documentRepository;
    private final String encryptionSecret;

    public DocumentServiceImpl(DocumentRepository documentRepository,
                               @Value("${document.encryption.secret}") String encryptionSecret) {
        this.documentRepository = documentRepository;
        this.encryptionSecret = encryptionSecret;
    }

    @Override
    public List<DocumentSummary> getAllDocuments(String username) {
        return documentRepository.findAllByOwnerUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Document getDocumentById(UUID id, String username) {
        Document document = documentRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        decryptData(document);
        return document;
    }

    @Override
    public void addDocument(MultipartFile file, String username) throws IOException {

        // some hard-coded validation: not empty, allowed MIME types, max size 256 MB

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        List<String> allowedTypes = List.of(
                "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
                "application/vnd.oasis.opendocument.text",  // .odt
                "text/plain", "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "text/csv", "application/vnd.ms-powerpoint",  // .ppt
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" // .pptx
        );
        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
        }

        long maxSize = 256 * 1024 * 1024; // 256 MB, max size * 1kb * 1mb
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File too large");
        }

        // sanitize filename before storing
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

        Document entity = new Document();
        entity.setId(UUID.randomUUID());
        entity.setFilename(filename);
        entity.setContentType(file.getContentType());
        entity.setUploadedAt(Instant.now());
        entity.setOwnerUsername(username);

        try {
            entity.setData(AesGcmEncryptor.encrypt(file.getBytes(), encryptionSecret));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Encryption failed", e);
        }

        documentRepository.save(entity);
    }

    // Decryption helper

    private void decryptData (@NonNull Document document){
        if (document.getData() != null && document.getData().length > 0) {
            try {
                document.setData(AesGcmEncryptor.decrypt(document.getData(), encryptionSecret));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Decryption failed", e);
            }
        }
    }

}
