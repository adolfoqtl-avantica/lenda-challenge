package com.lenda.challenge.repository.mongo;

import com.lenda.challenge.model.mongo.GameLog;
import com.lenda.challenge.model.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface GameLogRepository extends MongoRepository<GameLog, BigInteger> {

    GameLog findByUser(User user);

    GameLog findByGameId(Long gameId);
}
