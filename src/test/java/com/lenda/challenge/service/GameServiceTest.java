package com.lenda.challenge.service;

import com.lenda.challenge.model.postgres.Game;
import com.lenda.challenge.repository.mongo.GameLogRepository;
import com.lenda.challenge.repository.mongo.UserRepository;
import com.lenda.challenge.repository.postgres.GameRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    private Logger logMock;

    @Mock
    private GameRepository gameRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private GameLogRepository gameLogRepositoryMock;

    @InjectMocks
    private GameService gameService;

    @Before
    public void init() {
        gameService.dictionary.add("Hello");
        gameService.dictionary.add("World");
    }

    @Test
    public void test_CreateGame() {

        Mockito.when(gameRepositoryMock.save(Mockito.any(Game.class))).thenAnswer(
                invocationOnMock -> invocationOnMock.getArguments()[0]);

        Game game = gameService.create();

        Mockito.verify(gameRepositoryMock).save(game);

        Assert.assertEquals(new Integer(0), game.getScore());
        Assert.assertTrue(game.getBoard().size() > 0);
    }

    @Test(expected = GameService.InvalidPlayException.class)
    public void test_InvalidWord() throws GameService.GameException {

        Mockito.when(gameRepositoryMock.save(Mockito.any(Game.class))).thenAnswer(
                invocationOnMock -> invocationOnMock.getArguments()[0]);
        Game game = gameService.create();

        gameService.play(game, "SomeInvalidWord");
    }
}
