package com.kafetzisthomas.springvault.service;

import com.kafetzisthomas.springvault.entity.Document;
import com.kafetzisthomas.springvault.entity.EncryptionKey;
import com.kafetzisthomas.springvault.repository.DocumentRepository;
import com.kafetzisthomas.springvault.repository.DocumentSummary;
import com.kafetzisthomas.springvault.repository.EncryptionKeyRepository;
import com.kafetzisthomas.springvault.security.AesGcmEncryptor;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.NonNull;
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
    private final EncryptionKeyRepository encryptionKeyRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, EncryptionKeyRepository encryptionKeyRepository) {
        this.documentRepository = documentRepository;
        this.encryptionKeyRepository = encryptionKeyRepository;
    }

    @Override
    public List<DocumentSummary> getAllDocuments(String username) {
        return documentRepository.findAllByOwnerUsernameOrderByUploadedAtDesc(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Document getDocumentById(UUID id, String username) {
        Document document = documentRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        decryptData(Objects.requireNonNull(document, "Document must not be null"));
        return document;
    }

    @Override
    public void addDocument(MultipartFile file, String username) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        long maxSize = 256 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File too large");
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

        // sanitize filename before storing
        String filename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

        Document entity = new Document();
        entity.setFilename(filename);
        entity.setContentType(file.getContentType());
        entity.setUploadedAt(Instant.now());
        entity.setOwnerUsername(username);

        try {
            String key = getEncryptionKeyForUser(username);
            entity.setData(AesGcmEncryptor.encrypt(file.getBytes(), key));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Encryption failed", e);
        }
        documentRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteDocument(UUID id, String username) {
        long deleted = documentRepository.deleteByIdAndOwnerUsername(id, username);
        if (deleted == 0) {
            // either not found or not owned treat as 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
    }

    private String getEncryptionKeyForUser(String username) {
        EncryptionKey encryptionKey = encryptionKeyRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Encryption key not found for user: " + username));
        return encryptionKey.getEncryptedKey();
    }

    private void decryptData(@NonNull Document document){
        if (document.getData() != null && document.getData().length > 0) {
            try {
                String key = getEncryptionKeyForUser(document.getOwnerUsername());
                document.setData(AesGcmEncryptor.decrypt(document.getData(), key));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Decryption failed", e);
            }
        }
    }
}
