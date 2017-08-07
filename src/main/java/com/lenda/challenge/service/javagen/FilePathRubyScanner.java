package com.lenda.challenge.service.javagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lenda.challenge.service.ruby.RubyEnumDef;
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

    private List<RubyEnumDef> enumDefs;

    public FilePathRubyScanner() {
        rubyParsingService = new RubyParsingService();
        rubyParsingService.setRubyParser(new Parser());
        rubyParsingService.setRubyParserConfiguration(new ParserConfiguration(0, CompatVersion.RUBY2_0));
        classDefs = Lists.newArrayList();
        classDefsByName = Maps.newHashMap();
        enumDefs = Lists.newArrayList();
    }

    public void scan(String filePath, String basePackage) throws FileNotFoundException {
        List<String> rubyFilePaths = Lists.newArrayList(FileUtil.listFiles(new File(filePath)));

        for (String rubyFilePath : rubyFilePaths) {
            String modelPackage = basePackage + (rubyFilePath.contains("/") ? "." + rubyFilePath.substring(0, rubyFilePath.lastIndexOf("/")).replaceAll("\\/", "\\.") : "");
            RubyModelClassDef rubyModelClassDef = rubyParsingService.parseRubyClassModel(
                    modelPackage, filePath + rubyFilePath);
            if (rubyModelClassDef.getFields().size() == 0) {
                RubyEnumDef rubyEnumDef = rubyParsingService.parseRubyEnum(
                        modelPackage, filePath + rubyFilePath);
                if (rubyEnumDef.getConstants().size() == 0) {
                    continue;
                }
                enumDefs.add(rubyEnumDef);
                continue;
            }
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

    public List<RubyEnumDef> getEnumDefs() {
        return enumDefs;
    }

    public void setEnumDefs(List<RubyEnumDef> enumDefs) {
        this.enumDefs = enumDefs;
    }
}
