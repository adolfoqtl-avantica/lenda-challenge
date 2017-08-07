package com.lenda.challenge.service.javagen;

import com.google.common.collect.ImmutableMap;
import com.lenda.challenge.model.mongo.DocumentBase;
import com.lenda.challenge.service.ruby.RubyModelClassDef;
import com.lenda.challenge.service.ruby.RubyModelFieldDef;
import com.lenda.challenge.util.JavaWriter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Generates Spring Data Mongo DB java models.
 */
public class JavaMongoModelGenerator {

    private static final Map<String, String> RUBY_CLASS_JAVA_CLASS_MAPPING = ImmutableMap.<String, String>builder()
            .put("Time", "java.time.LocalTime")
            .put("DateTime", "java.time.OffsetDateTime")
            .put("Array", "java.util.List")
            .build();

    public void generate() throws IOException, ClassNotFoundException {
        FilePathRubyScanner scanner = new FilePathRubyScanner();
        scanner.scan("./src/main/resources/ruby/", "com.lenda.challenge.model.mongo");

        String modelsFilePath = "./src/main/java/";
        for (RubyModelClassDef rubyModelClassDef : scanner.getClassDefs()) {
            File file = new File(modelsFilePath + rubyModelClassDef.getPackageName().replaceAll("\\.", "\\/") + "/" + rubyModelClassDef.getClassName() + ".java");
            file.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(file);
            generateRubyModel(scanner, rubyModelClassDef, fileWriter);
            fileWriter.flush();
        }
    }

    private void generateRubyModel(FilePathRubyScanner scanner, RubyModelClassDef rubyModelClassDef, Writer writer) throws IOException, ClassNotFoundException {
        JavaWriter javaWriter = new JavaWriter(writer);

        javaWriter.packageDecl(rubyModelClassDef.getPackageName());
        javaWriter.nl();

        javaWriter.imports(Document.class);
        if (rubyModelClassDef.hasDbRefs()) {
            javaWriter.imports(DBRef.class);
        }
        for (Class<?> fieldImport : rubyModelClassDef.getFieldJavaImports(RUBY_CLASS_JAVA_CLASS_MAPPING)) {
            javaWriter.imports(fieldImport);
        }
        for (RubyModelClassDef fieldClassDef : rubyModelClassDef.getFieldModelImports(scanner)) {
            javaWriter.imports(fieldClassDef.getPackageName() + "." + fieldClassDef.getClassName());
        }

        javaWriter.annotation(Document.class);
        javaWriter.beginClass(rubyModelClassDef.getClassName(), DocumentBase.class.getSimpleName());

        for (RubyModelFieldDef rubyModelFieldDef : rubyModelClassDef.getFields()) {
            if (rubyModelFieldDef.getRef()) {
                javaWriter.annotation(DBRef.class);
            }
            javaWriter.privateField(getFieldType(rubyModelFieldDef.getFieldType()), rubyModelFieldDef.getFieldName());
        }

        for (RubyModelFieldDef rubyModelFieldDef : rubyModelClassDef.getFields()) {
            javaWriter.beginPublicMethod(getFieldType(rubyModelFieldDef.getFieldType()), getGetterMethodName(rubyModelFieldDef));
            javaWriter.line(String.format("return this.%s;", rubyModelFieldDef.getFieldName()));
            javaWriter.end();
            javaWriter.nl();
            javaWriter.beginPublicMethod("void", getSetterMethodName(rubyModelFieldDef), getFieldType(rubyModelFieldDef.getFieldType()) + " " + rubyModelFieldDef.getFieldName());
            javaWriter.line(String.format("this.%s = %s;", rubyModelFieldDef.getFieldName(), rubyModelFieldDef.getFieldName()));
            javaWriter.end();
        }
        
        javaWriter.end();
    }

    private String getFieldType(String rubyFieldType) {
        String javaFieldType = RUBY_CLASS_JAVA_CLASS_MAPPING.get(rubyFieldType);
        if (javaFieldType != null) {
            return javaFieldType.equals("java.util.List")
                    ? String.format("List<%s>", rubyFieldType)
                    : javaFieldType.substring(javaFieldType.lastIndexOf(".") + 1);
        } else {
            return rubyFieldType;
        }
    }

    private String getGetterMethodName(RubyModelFieldDef rubyModelFieldDef) {
        return (rubyModelFieldDef.getFieldType().equals("Boolean") ? "is" : "get")
                + rubyModelFieldDef.getFieldName().substring(0, 1).toUpperCase() + rubyModelFieldDef.getFieldName().substring(1);
    }

    private String getSetterMethodName(RubyModelFieldDef rubyModelFieldDef) {
        return "set" + rubyModelFieldDef.getFieldName().substring(0, 1).toUpperCase() + rubyModelFieldDef.getFieldName().substring(1);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JavaMongoModelGenerator generator = new JavaMongoModelGenerator();
        generator.generate();
    }
}
