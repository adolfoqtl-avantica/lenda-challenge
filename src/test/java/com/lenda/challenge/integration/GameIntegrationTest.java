package com.lenda.challenge.integration;

import com.lenda.challenge.model.mongo.GameLog;
import com.lenda.challenge.model.postgres.Game;
import com.lenda.challenge.repository.mongo.GameLogRepository;
import com.lenda.challenge.service.GameService;
import com.lenda.challenge.spring.BackEndIntegrationTest;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.OffsetDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@BackEndIntegrationTest
public class GameIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameLogRepository gameLogRepository;

    @Test
    @FlywayTest
    public void testGameService() {

        Game game = gameService.create();

        Game readGame = gameService.get(game.getId());
        Assert.assertEquals(game.getId(), readGame.getId());

        GameLog gameLog = gameLogRepository.findByGameId(readGame.getId());
        Assert.assertEquals(game.getId(), gameLog.getGameId());
        Assert.assertEquals("Test", gameLog.getUser().getFirstName());
        Assert.assertEquals(OffsetDateTime.now().toLocalDate(), gameLog.getGameTimestamp().toLocalDate());
        Assert.assertEquals(OffsetDateTime.now().toLocalTime().getHour(), gameLog.getGameTimestamp().toLocalTime().getHour());
    }
}
