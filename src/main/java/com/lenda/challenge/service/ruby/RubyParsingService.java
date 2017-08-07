package com.lenda.challenge.service.ruby;

import org.jrubyparser.Parser;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

@Service
public class RubyParsingService {

    @Autowired
    private Parser rubyParser;

    @Autowired
    private ParserConfiguration rubyParserConfiguration;

    public RubyModelClassDef parseRubyModel(String packageName, String modelFilePath) throws FileNotFoundException {
        InputStream modelFileResourceStream = this.getClass().getClassLoader().getResourceAsStream(modelFilePath);
        Node node = parseRuby(modelFileResourceStream != null
                ? new InputStreamReader(modelFileResourceStream) : new FileReader(new File(modelFilePath)));
        return new RubyModelClassDef(packageName, GetClassNameVisitor.getClassName(node), ClassFieldsVisitor.findClassFields(node));
    }

    private Node parseRuby(String content) {
        return rubyParser.parse("<code>", new StringReader(content), rubyParserConfiguration);
    }

    private Node parseRuby(Reader reader) {
        return rubyParser.parse("<code>", reader, rubyParserConfiguration);
    }

    public void setRubyParser(Parser rubyParser) {
        this.rubyParser = rubyParser;
    }

    public void setRubyParserConfiguration(ParserConfiguration rubyParserConfiguration) {
        this.rubyParserConfiguration = rubyParserConfiguration;
    }
}
