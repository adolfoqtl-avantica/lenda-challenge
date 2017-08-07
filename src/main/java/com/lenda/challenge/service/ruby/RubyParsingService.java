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

    public RubyModelClassDef parseRubyClassModel(String packageName, String rubyFilePath) throws FileNotFoundException {
        Node node = parseRuby(getRubySourceReader(rubyFilePath));
        return new RubyModelClassDef(packageName, GetClassNameVisitor.getClassName(node), GetClassFieldsVisitor.findClassFields(node));
    }

    public RubyEnumDef parseRubyEnum(String packageName, String rubyFilePath) throws FileNotFoundException {
        Node node = parseRuby(getRubySourceReader(rubyFilePath));
        return new RubyEnumDef(packageName, GetEnumNameVisitor.getEnumName(node), GetEnumConstantsVisitor.findEnumConstants(node.childNodes().get(0)));
    }

    private Node parseRuby(String content) {
        return rubyParser.parse("<code>", new StringReader(content), rubyParserConfiguration);
    }

    private Node parseRuby(Reader reader) {
        return rubyParser.parse("<code>", reader, rubyParserConfiguration);
    }

    private Reader getRubySourceReader(String rubyFilePath) throws FileNotFoundException {
        InputStream rubyFileResourceStream = this.getClass().getClassLoader().getResourceAsStream(rubyFilePath);
        return rubyFileResourceStream != null
                ? new InputStreamReader(rubyFileResourceStream)
                : new FileReader(new File(rubyFilePath));
    }

    public void setRubyParser(Parser rubyParser) {
        this.rubyParser = rubyParser;
    }

    public void setRubyParserConfiguration(ParserConfiguration rubyParserConfiguration) {
        this.rubyParserConfiguration = rubyParserConfiguration;
    }
}
