package com.lenda.challenge.service.javagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lenda.challenge.service.ruby.RubyModelClassDef;
import com.lenda.challenge.service.ruby.RubyParsingService;
import org.aspectj.util.FileUtil;
import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser;
import org.jrubyparser.parser.ParserConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Scans a file path for Ruby source files.
 */
public class FilePathRubyScanner {

    private RubyParsingService rubyParsingService;

    private List<RubyModelClassDef> classDefs;
    private Map<String, RubyModelClassDef> classDefsByName;

    public FilePathRubyScanner() {
        rubyParsingService = new RubyParsingService();
        rubyParsingService.setRubyParser(new Parser());
        rubyParsingService.setRubyParserConfiguration(new ParserConfiguration(0, CompatVersion.RUBY2_0));
        classDefs = Lists.newArrayList();
        classDefsByName = Maps.newHashMap();
    }

    public void scan(String filePath, String basePackage) throws FileNotFoundException {
        List<String> rubyModelPaths = Lists.newArrayList(FileUtil.listFiles(new File(filePath)));

        for (String rubyModelPath : rubyModelPaths) {
            RubyModelClassDef rubyModelClassDef = rubyParsingService.parseRubyModel(
                    basePackage + (rubyModelPath.contains("/") ? "." + rubyModelPath.substring(0, rubyModelPath.lastIndexOf("/")).replaceAll("\\/", "\\.") : ""),
                    filePath + rubyModelPath);
            classDefs.add(rubyModelClassDef);
            classDefsByName.put(rubyModelClassDef.getClassName(), rubyModelClassDef);
        }
    }

    public List<RubyModelClassDef> getClassDefs() {
        return this.classDefs;
    }

    public RubyModelClassDef getClassDef(String className) {
        return this.classDefsByName.get(className);
    }
}
