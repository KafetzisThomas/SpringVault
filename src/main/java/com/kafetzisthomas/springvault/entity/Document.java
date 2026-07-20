package com.kafetzisthomas.springvault.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @NotNull(message="is required")
    @Column(nullable = false)
    private String filename;

    @NotNull(message="is required")
    @Column(nullable = false)
    private String contentType;

    @Lob
    @NotNull(message="is required")
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data;

    @NotNull(message="is required")
    @Column(nullable = false)
    private Instant uploadedAt;

    @NotNull(message="is required")
    @Column(nullable = false)
    private String ownerUsername;

    // contructors
    public Document() {}

    public Document(String filename, String contentType, byte[] data, Instant uploadedAt, String ownerUsername) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.uploadedAt = uploadedAt;
        this.ownerUsername = ownerUsername;
    }

    // getters/setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    @Override
    public String toString() {
        return "Document [id=" + id + ", filename=" + filename + ", contentType=" + contentType +
                ", uploadedAt=" + uploadedAt + ", ownerUsername=" + ownerUsername + "]";
    }

}
