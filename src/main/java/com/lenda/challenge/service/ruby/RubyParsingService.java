package com.lenda.challenge.service.ruby;

import org.jrubyparser.Parser;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.io.StringReader;

@Service
public class RubyParsingService {

    @Autowired
    private Parser rubyParser;

    @Autowired
    private ParserConfiguration rubyParserConfiguration;

    public Node parseRuby(String content) {
        return rubyParser.parse("<code>", new StringReader(content), rubyParserConfiguration);
    }

    public Node parseRuby(Reader reader) {
        return rubyParser.parse("<code>", reader, rubyParserConfiguration);
    }
}
