package com.lenda.challenge.service.javagen;

import com.lenda.challenge.service.ruby.RubyEnumDef;
import com.lenda.challenge.service.ruby.RubyModelClassDef;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates Spring Data Mongo DB java models.
 */
public class JavaMongoModelGenerator {


    private void generate() throws IOException, ClassNotFoundException {
        FilePathRubyScanner scanner = new FilePathRubyScanner();
        scanner.scan("./src/main/resources/ruby/", "com.lenda.challenge.model.mongo2");

        String modelsFilePath = "./src/main/java/";

        ModelGenerator modelGenerator = new ModelGenerator();
        for (RubyModelClassDef rubyModelClassDef : scanner.getClassDefs()) {
            File file = new File(modelsFilePath + rubyModelClassDef.getPackageName().replaceAll("\\.", "\\/") + "/" + rubyModelClassDef.getClassName() + ".java");
            file.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(file);
            modelGenerator.generateModel(scanner, rubyModelClassDef, fileWriter);
            fileWriter.flush();
        }

        EnumGenerator enumGenerator = new EnumGenerator();
        for (RubyEnumDef rubyEnumDef : scanner.getEnumDefs()) {
            File file = new File(modelsFilePath + rubyEnumDef.getPackageName().replaceAll("\\.", "\\/") + "/" + rubyEnumDef.getEnumName() + ".java");
            file.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(file);
            enumGenerator.generateEnum(rubyEnumDef, fileWriter);
            fileWriter.flush();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JavaMongoModelGenerator generator = new JavaMongoModelGenerator();
        //generator.generate();
    }
}
