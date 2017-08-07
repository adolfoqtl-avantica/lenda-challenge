package com.lenda.challenge.service.ruby;

public class RubyEnumConstantDef {

    private String constant;
    private String value;

    RubyEnumConstantDef() {
    }

    RubyEnumConstantDef(String constant, String value) {
        this.constant = constant;
        this.value = value;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
