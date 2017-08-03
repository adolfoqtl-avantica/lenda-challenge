package com.lenda.challenge.service.ruby;

import com.google.common.collect.Lists;

import java.util.List;

public class RubyModelFieldDef {

    final static List<String> VALID_FIELD_DEFS = Lists.newArrayList("field", "embeds_one", "embeds_many", "has_one", "has_many");

    private String fieldName;
    private String fieldType;
    private Boolean isRef;

    RubyModelFieldDef() {
    }

    RubyModelFieldDef(String fieldName, String fieldType, Boolean isRef) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isRef = isRef;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getRef() {
        return isRef;
    }

    public void setRef(Boolean ref) {
        isRef = ref;
    }
}
