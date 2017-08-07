package com.lenda.challenge.service.ruby;

import java.util.List;

public class RubyEnumDef {

    private String packageName;
    private String enumName;
    private List<RubyEnumConstantDef> constants;

    public RubyEnumDef() {
    }

    public RubyEnumDef(String packageName, String enumName, List<RubyEnumConstantDef> constants) {
        this.packageName = packageName;
        this.enumName = enumName;
        this.constants = constants;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public List<RubyEnumConstantDef> getConstants() {
        return constants;
    }

    public void setConstants(List<RubyEnumConstantDef> constants) {
        this.constants = constants;
    }
}
