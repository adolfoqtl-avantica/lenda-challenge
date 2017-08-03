package com.lenda.challenge.service.ruby;

import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStreamReader;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RubyParsingServiceTest {

    @Spy
    private Parser rubyParserSpy = new Parser();

    @Spy
    private ParserConfiguration rubyParserConfigurationSpy = new ParserConfiguration(0, CompatVersion.RUBY2_0);

    @InjectMocks
    private RubyParsingService rubyParsingService;

    private InputStreamReader accountReader;

    @Before
    public void init() {
        accountReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("ruby/account.rb"));
    }

    @Test
    public void test_ParseRuby() {
        Node node = rubyParsingService.parseRuby(accountReader);

        List<RubyModelFieldDef> rubyModelFieldDefs = ClassFieldsVisitor.findClassFields(node);
        for (RubyModelFieldDef rubyModelFieldDef : rubyModelFieldDefs) {
            System.out.println(String.format("%s %s %s", rubyModelFieldDef.getFieldType(), rubyModelFieldDef.getFieldName(), rubyModelFieldDef.getRef()));
        }
    }
}
