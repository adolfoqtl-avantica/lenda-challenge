package com.lenda.challenge.model.mongo;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class GameLog extends DocumentBase {

    @DBRef(lazy = true)
    private User user;
    private Long gameId;
    private LocalDateTime gameTimestamp;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getGameTimestamp() {
        return gameTimestamp;
    }

    public void setGameTimestamp(LocalDateTime gameTimestamp) {
        this.gameTimestamp = gameTimestamp;
    }
}
