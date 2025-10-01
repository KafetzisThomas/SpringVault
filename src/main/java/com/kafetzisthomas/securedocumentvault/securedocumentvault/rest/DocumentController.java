package com.kafetzisthomas.securedocumentvault.securedocumentvault.rest;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.DocumentSummary;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String listDocuments(HttpServletRequest request, Model model, Principal principal) throws IOException {

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
    public String addDocument(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        try {
            documentService.addDocument(file);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Document uploaded successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Upload failed: " + e.getMessage());
        }

        return "redirect:/";
    }

}
