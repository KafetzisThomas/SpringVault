package com.kafetzisthomas.springvault.rest;

import com.kafetzisthomas.springvault.dao.DocumentSummary;
import com.kafetzisthomas.springvault.entity.Document;
import com.kafetzisthomas.springvault.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String listDocuments(HttpServletRequest request, Model model, Principal principal) {

        // get the documents from db only for the current user
        List<DocumentSummary> documents = documentService.getAllDocuments(principal.getName());

        model.addAttribute("documents", documents);
        model.addAttribute("request", request);

        return "documents/document-report";
    }

    @GetMapping("/document/upload")
    public String showUploadForm() {
        return "documents/upload-document";
    }

    @PostMapping("/document/add")
    public String addDocument(@RequestParam("file") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            documentService.addDocument(file, principal.getName());
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Document uploaded successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Upload failed: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/document/view")
    public ResponseEntity<ByteArrayResource> viewDocument(@RequestParam("id") UUID id, Principal principal) {
        Document document = documentService.getDocumentById(id, principal.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Objects.requireNonNull(document.getContentType())))
                .header(
                        "Content-Disposition",
                        "inline; filename=\"" + document.getFilename() + "\"")
                .body(new ByteArrayResource(Objects.requireNonNull(document.getData())));
    }

    @GetMapping("/document/download")
    public ResponseEntity<ByteArrayResource> downloadDocument(@RequestParam("id") UUID id, Principal principal) {
        Document document = documentService.getDocumentById(id, principal.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Objects.requireNonNull(document.getContentType())))
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"" + document.getFilename() + "\"")
                .body(new ByteArrayResource(Objects.requireNonNull(document.getData())));
    }
}
