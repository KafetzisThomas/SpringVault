package com.kafetzisthomas.springvault.rest;

import com.kafetzisthomas.springvault.entity.Document;
import com.kafetzisthomas.springvault.repository.DocumentSummary;
import com.kafetzisthomas.springvault.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    private final Principal principal = () -> "testuser";

    @Test
    void whenListDocuments_thenShowDocumentDashboard() throws Exception {
        DocumentSummary summary = mock(DocumentSummary.class);
        when(summary.getId()).thenReturn(UUID.randomUUID());
        when(summary.getFilename()).thenReturn("test.txt");
        when(summary.getContentType()).thenReturn("text/plain");
        when(summary.getUploadedAt()).thenReturn(java.time.Instant.now());

        when(documentService.getAllDocuments("testuser")).thenReturn(List.of(summary));
        mockMvc.perform(get("/").principal(Objects.requireNonNull(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("documents/dashboard"))
                .andExpect(model().attributeExists("documents", "request"));
    }

    @Test
    void whenDocumentUploadSuccess_thenRedirectWithSuccessMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        mockMvc.perform(multipart("/document/add").file(file).principal(Objects.requireNonNull(principal)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("successMessage"));
        verify(documentService).addDocument(any(), eq("testuser"));
    }

    @Test
    void whenDocumentUploadFails_thenRedirectWithErrorMessage() throws Exception {
        doThrow(new RuntimeException("fail")).when(documentService).addDocument(any(), any());
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "data".getBytes());
        mockMvc.perform(multipart("/document/add").file(file).principal(Objects.requireNonNull(principal)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void whenViewDocument_thenReturnDocument() throws Exception {
        UUID id = UUID.randomUUID();
        Document doc = new Document();
        doc.setContentType("text/plain");
        doc.setFilename("test.txt");
        doc.setData("testData".getBytes());

        when(documentService.getDocumentById(id, "testuser")).thenReturn(doc);
        mockMvc.perform(get("/document/view").param("id", id.toString()).principal(Objects.requireNonNull(principal)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.txt\""))
                .andExpect(content().contentType("text/plain"));
    }

    @Test
    void whenDownloadDocument_thenReturnAttachment() throws Exception {
        UUID id = UUID.randomUUID();
        Document doc = new Document();
        doc.setContentType("text/plain");
        doc.setFilename("test.txt");
        doc.setData("testData".getBytes());

        when(documentService.getDocumentById(id, "testuser")).thenReturn(doc);
        mockMvc.perform(get("/document/download").param("id", id.toString()).principal(Objects.requireNonNull(principal)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
                .andExpect(content().contentType("text/plain"));
    }
}
