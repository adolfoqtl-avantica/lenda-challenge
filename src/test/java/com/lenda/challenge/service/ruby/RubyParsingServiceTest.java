package com.lenda.challenge.service.ruby;

import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser;
import org.jrubyparser.parser.ParserConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class RubyParsingServiceTest {

    @Spy
    private Parser rubyParserSpy = new Parser();

    @Spy
    private ParserConfiguration rubyParserConfigurationSpy = new ParserConfiguration(0, CompatVersion.RUBY2_0);

    @InjectMocks
    private RubyParsingService rubyParsingService;

    @Before
    public void init() {

    }

    @Test
    public void test_ParseRuby() throws IOException, ClassNotFoundException {
        RubyModelClassDef rubyModelClassDef = rubyParsingService.parseRubyClassModel("com.lenda.challenge.model.mongo","ruby/account.rb");

        Assert.assertEquals("Account", rubyModelClassDef.getClassName());
        Assert.assertEquals("email", rubyModelClassDef.getFields().get(2).getFieldName());
    }
}
