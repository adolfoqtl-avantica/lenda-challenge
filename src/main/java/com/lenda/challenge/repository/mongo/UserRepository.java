package com.lenda.challenge.repository.mongo;

import com.lenda.challenge.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface UserRepository extends MongoRepository<User, BigInteger> {

    User findByEmail(String email);

    @Query("{ 0? : ?1 }")
    User findByAttributes(String key, String value);
}
