package com.kafetzisthomas.securedocumentvault.securedocumentvault.dao;

import java.time.LocalDateTime;

public interface DocumentSummary {
    Long getId();
    String getFilename();
    String getContentType();
    LocalDateTime getUploadedAt();
}
