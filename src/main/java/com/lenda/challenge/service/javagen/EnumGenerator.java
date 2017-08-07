package com.lenda.challenge.service.javagen;

import com.lenda.challenge.service.ruby.RubyEnumConstantDef;
import com.lenda.challenge.service.ruby.RubyEnumDef;
import com.lenda.challenge.util.JavaWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Generates an enum from a Ruby enum definition.
 */
public class EnumGenerator {

    void generateEnum(RubyEnumDef rubyEnumDef, Writer writer) throws IOException, ClassNotFoundException {
        JavaWriter javaWriter = new JavaWriter(writer);

        javaWriter.packageDecl(rubyEnumDef.getPackageName());
        javaWriter.nl();

        javaWriter.beginEnum(rubyEnumDef.getEnumName());

        Iterator<RubyEnumConstantDef> rubyEnumConstantIterator = rubyEnumDef.getConstants().iterator();
        while (rubyEnumConstantIterator.hasNext()) {
            RubyEnumConstantDef rubyEnumConstantDef = rubyEnumConstantIterator.next();
            javaWriter.line(rubyEnumConstantDef.getValue() + (rubyEnumConstantIterator.hasNext() ? "," : ";"));
        }

        javaWriter.end();
    }
}
