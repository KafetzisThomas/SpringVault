package com.kafetzisthomas.securedocumentvault.securedocumentvault.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService {

    void addDocument(MultipartFile file) throws IOException;

}
