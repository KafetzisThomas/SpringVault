package com.kafetzisthomas.springvault.repository;

import com.kafetzisthomas.springvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
