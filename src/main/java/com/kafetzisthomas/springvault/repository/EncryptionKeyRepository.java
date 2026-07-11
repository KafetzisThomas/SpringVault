package com.kafetzisthomas.springvault.repository;

import com.kafetzisthomas.springvault.entity.EncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// use following sql command to see if encryption works for file data formatted as OID:
// SELECT id, filename, encode(lo_get(data, 0, 100), 'hex') as encrypted_preview FROM document

public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, String> {

    Optional<EncryptionKey> findByUsername(String username);

}
