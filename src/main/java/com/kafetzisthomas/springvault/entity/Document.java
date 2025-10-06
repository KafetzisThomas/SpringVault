package com.kafetzisthomas.springvault.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Column
    private String contentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private Instant uploadedAt;

    @Column(nullable = false)
    private String ownerUsername;

    // define contractors
    public Document() {

    }

    public Document(String filename, String contentType, byte[] data, Instant uploadedAt, String ownerUsername) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.uploadedAt = uploadedAt;
        this.ownerUsername = ownerUsername;
    }

    // define getters/setters

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

    // define toString() method

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", data=" + Arrays.toString(data) +
                ", uploadedAt=" + uploadedAt +
                ", ownerUsername='" + ownerUsername + '\'' +
                '}';
    }

}
