package com.kafetzisthomas.springvault.service;

import com.kafetzisthomas.springvault.dao.DocumentRepository;
import com.kafetzisthomas.springvault.dao.DocumentSummary;
import com.kafetzisthomas.springvault.dao.EncryptionKeyRepository;
import com.kafetzisthomas.springvault.entity.Document;
import com.kafetzisthomas.springvault.entity.EncryptionKey;
import com.kafetzisthomas.springvault.security.AesGcmEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private EncryptionKeyRepository encryptionKeyRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDocuments_returnList() {
        List<DocumentSummary> mockList = List.of(mock(DocumentSummary.class));
        when(documentRepository.findAllByOwnerUsername("testuser")).thenReturn(mockList);

        var result = documentService.getAllDocuments("testuser");

        assertEquals(1, result.size());
        verify(documentRepository).findAllByOwnerUsername("testuser");
    }

    @Test
    void getDocumentById_decryptAndReturn() {
        Document doc = new Document();
        doc.setId(UUID.randomUUID());
        doc.setOwnerUsername("testuser");
        doc.setData("encData".getBytes());

        EncryptionKey key = new EncryptionKey();
        key.setEncryptedKey("key123");

        when(documentRepository.findByIdAndOwnerUsername(any(), eq("testuser"))).thenReturn(Optional.of(doc));
        when(encryptionKeyRepository.findByUsername("testuser")).thenReturn(Optional.of(key));

        try (MockedStatic<AesGcmEncryptor> mock = mockStatic(AesGcmEncryptor.class)) {
            mock.when(() -> AesGcmEncryptor.decrypt(any(), any())).thenReturn("decData".getBytes());

            Document result = documentService.getDocumentById(doc.getId(), "testuser");

            assertArrayEquals("decData".getBytes(), result.getData());
        }
    }

    @Test
    void addDocument_saveEncryptedFile() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getSize()).thenReturn(100L);
        when(file.getBytes()).thenReturn("testData".getBytes());

        EncryptionKey key = new EncryptionKey();
        key.setEncryptedKey("key123");
        when(encryptionKeyRepository.findByUsername("testuser")).thenReturn(Optional.of(key));

        try (MockedStatic<AesGcmEncryptor> mock = mockStatic(AesGcmEncryptor.class)) {
            mock.when(() -> AesGcmEncryptor.encrypt(any(), any())).thenReturn("encData".getBytes());

            documentService.addDocument(file, "testuser");

            verify(documentRepository).save(any(Document.class));
        }
    }

    @Test
    void addDocument_throwErrorWhenFileEmpty() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> documentService.addDocument(file, "testuser"));
    }

    @Test
    void addDocument_throwErrorWhenUnsupportedType() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("img.png");

        assertThrows(IllegalArgumentException.class, () -> documentService.addDocument(file, "testuser"));
    }

    @Test
    void addDocument_throwErrorWhenEncryptionFails() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getSize()).thenReturn(100L);
        when(file.getBytes()).thenReturn("testData".getBytes());

        EncryptionKey key = new EncryptionKey();
        key.setEncryptedKey("key123");
        when(encryptionKeyRepository.findByUsername("testuser")).thenReturn(Optional.of(key));

        try (MockedStatic<AesGcmEncryptor> mock = mockStatic(AesGcmEncryptor.class)) {
            mock.when(() -> AesGcmEncryptor.encrypt(any(), any())).thenThrow(new RuntimeException("fail"));

            assertThrows(ResponseStatusException.class, () -> documentService.addDocument(file, "testuser"));
        }
    }

}
