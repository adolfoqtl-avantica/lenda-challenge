package com.lenda.challenge.service.javagen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.lenda.challenge.model.mongo.DocumentBase;
import com.lenda.challenge.service.ruby.RubyModelClassDef;
import com.lenda.challenge.service.ruby.RubyModelFieldDef;
import com.lenda.challenge.util.JavaWriter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates a data model from a Ruby class definition.
 */
public class ModelGenerator {

    private static final Map<String, Class<?>> RUBY_CLASS_JAVA_CLASS_MAPPING = ImmutableMap.<String, Class<?>>builder()
            .put("Time", LocalTime.class)
            .put("DateTime", OffsetDateTime.class)
            .put("Array", List.class)
            .build();

    private static final Set<String> RESERVED_CLASSES = Sets.newHashSet(
            Document.class,
            DBRef.class).stream()
            .map(reservedClass -> reservedClass.getSimpleName())
            .collect(Collectors.toSet());

    void generateModel(FilePathRubyScanner scanner, RubyModelClassDef rubyModelClassDef, Writer writer) throws IOException, ClassNotFoundException {
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
            javaWriter.privateField(getFieldType(scanner, rubyModelFieldDef.getFieldType()), rubyModelFieldDef.getFieldName());
        }

        for (RubyModelFieldDef rubyModelFieldDef : rubyModelClassDef.getFields()) {
            javaWriter.beginPublicMethod(getFieldType(scanner, rubyModelFieldDef.getFieldType()), getGetterMethodName(rubyModelFieldDef));
            javaWriter.line(String.format("return this.%s;", rubyModelFieldDef.getFieldName()));
            javaWriter.end();
            javaWriter.nl();
            javaWriter.beginPublicMethod("void", getSetterMethodName(rubyModelFieldDef), getFieldType(scanner, rubyModelFieldDef.getFieldType()) + " " + rubyModelFieldDef.getFieldName());
            javaWriter.line(String.format("this.%s = %s;", rubyModelFieldDef.getFieldName(), rubyModelFieldDef.getFieldName()));
            javaWriter.end();
        }

        javaWriter.end();
    }

    private String getFieldType(FilePathRubyScanner scanner, String rubyFieldType) {
        Class<?> javaFieldType = RUBY_CLASS_JAVA_CLASS_MAPPING.get(rubyFieldType);
        if (javaFieldType != null) {
            return javaFieldType.equals(List.class)
                    ? String.format("List<%s>", rubyFieldType)
                    : javaFieldType.getSimpleName();
        } else if (RESERVED_CLASSES.contains(rubyFieldType)
                || (rubyFieldType.startsWith("List") && RESERVED_CLASSES.contains(rubyFieldType.substring(rubyFieldType.indexOf("<") + 1, rubyFieldType.lastIndexOf(">"))))) {
            RubyModelClassDef fieldClassDef = scanner.getClassDef(rubyFieldType);
            return fieldClassDef != null
                    ? fieldClassDef.getPackageName() + "." + fieldClassDef.getClassName()
                    : rubyFieldType;
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

}
