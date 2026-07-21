package com.kafetzisthomas.springvault.repository;

import java.time.Instant;
import java.util.UUID;

public interface DocumentSummary {

    UUID getId();

    String getFilename();

    String getContentType();

    Instant getUploadedAt();

}
