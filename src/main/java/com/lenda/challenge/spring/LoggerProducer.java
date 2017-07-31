package com.lenda.challenge.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerProducer {

    @Bean
    public Logger provideLogger() {
        return LoggerFactory.getLogger(LoggerProducer.class);
    }
}
