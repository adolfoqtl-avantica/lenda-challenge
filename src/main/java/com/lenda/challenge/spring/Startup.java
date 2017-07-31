package com.lenda.challenge.spring;

import com.lenda.challenge.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Startup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private GameService gameService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        gameService.loadDictionary();
    }
}
