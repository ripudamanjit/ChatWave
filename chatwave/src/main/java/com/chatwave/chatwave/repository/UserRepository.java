package com.chatwave.chatwave.repository;

import com.chatwave.chatwave.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
    boolean existsByUsername(String username);
}