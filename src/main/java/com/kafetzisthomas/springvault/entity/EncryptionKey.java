package com.kafetzisthomas.springvault.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "encryption_keys")
public class EncryptionKey {

    @Id
    private String username;

    @NotNull(message="is required")
    @Column(length = 256, nullable = false)
    private String encryptedKey;

    // contructors
    public EncryptionKey() {}

    public EncryptionKey(String username, String encryptedKey) {
        this.username = username;
        this.encryptedKey = encryptedKey;
    }

    // getters/setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    @Override
    public String toString() {
        return "EncryptionKey [username=" + username + ", encryptedKey=***]";
    }

}
