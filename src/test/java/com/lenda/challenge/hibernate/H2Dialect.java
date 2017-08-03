package com.lenda.challenge.hibernate;

import java.sql.Types;

public class H2Dialect extends org.hibernate.dialect.H2Dialect {

    public H2Dialect() {
        registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
