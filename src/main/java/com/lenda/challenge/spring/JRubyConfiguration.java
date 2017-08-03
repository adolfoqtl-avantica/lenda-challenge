package com.lenda.challenge.spring;

import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser;
import org.jrubyparser.parser.ParserConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JRubyConfiguration {

    @Bean
    public Parser parseRuby() {
        return new Parser();
    }

    @Bean
    public ParserConfiguration provideRubyParserConfig() {
        return new ParserConfiguration(0, CompatVersion.RUBY2_0);
    }
}
