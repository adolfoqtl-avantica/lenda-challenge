package com.lenda.challenge.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @Mock
    private Logger logMock;

    @InjectMocks
    private GameService gameService;

    @Before
    public void init() {

    }

    @Test
    public void testGameService() {

    }

}
