package com.lenda.challenge.service.ruby;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lenda.challenge.service.javagen.FilePathRubyScanner;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RubyModelClassDef {

    private String packageName;
    private String className;
    private List<RubyModelFieldDef> fields;

    public RubyModelClassDef() {
    }

    public RubyModelClassDef(String packageName, String className, List<RubyModelFieldDef> fields) {
        this.packageName = packageName;
        this.className = className;
        this.fields = fields;
    }

    public Boolean hasDbRefs() {
        for (RubyModelFieldDef field : fields) {
            if (field.getRef()) {
                return true;
            }
        }
        return false;
    }

    public Set<Class<?>> getFieldJavaImports(Map<String, Class<?>> rubyJavaMappings) throws ClassNotFoundException {
        Set<Class<?>> fieldImports = Sets.newHashSet();
        for (RubyModelFieldDef field : fields) {
            if (rubyJavaMappings.get(field.getFieldType()) != null) {
                fieldImports.add(rubyJavaMappings.get(field.getFieldType()));
                continue;
            }
            if (field.getFieldType().startsWith("List")) {
                fieldImports.add(List.class);
            }
        }
        return fieldImports;
    }

    public List<RubyModelClassDef> getFieldModelImports(FilePathRubyScanner scanner) {
        List<RubyModelClassDef> fieldImports = Lists.newArrayList();
        for (RubyModelFieldDef field : fields) {
            RubyModelClassDef fieldClassDef = scanner.getClassDef(field.getFieldType());
            if (fieldClassDef != null && !fieldClassDef.getPackageName().equals(packageName)) {
                fieldImports.add(fieldClassDef);
            }
        }
        return fieldImports;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<RubyModelFieldDef> getFields() {
        return fields;
    }

    public void setFields(List<RubyModelFieldDef> fields) {
        this.fields = fields;
    }
}
