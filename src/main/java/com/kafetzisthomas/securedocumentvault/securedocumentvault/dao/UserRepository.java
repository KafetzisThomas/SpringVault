package com.kafetzisthomas.securedocumentvault.securedocumentvault.dao;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
}
