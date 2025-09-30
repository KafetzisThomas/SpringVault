package com.kafetzisthomas.securedocumentvault.securedocumentvault.rest;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/document/upload")
    public String showUploadForm() {
        return "documents/upload-document";
    }

    @PostMapping("/document/add")
    public String addDocument(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        try {
            documentService.addDocument(file);
            model.addAttribute("message", "Document uploaded successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Upload failed: " + e.getMessage());
        }

        return "documents/upload-document";
    }

}
