package com.lenda.challenge.integration;

import com.lenda.challenge.core.BackEndIntegrationTest;
import com.lenda.challenge.service.GameService;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@BackEndIntegrationTest
public class GameIntegrationTest {

    @Autowired
    private GameService gameService;

    @Before
    public void init() throws Exception {

    }

    @Test
    @FlywayTest
    public void testGameService() {

    }
}
