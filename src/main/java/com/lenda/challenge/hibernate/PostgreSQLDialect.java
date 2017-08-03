package com.lenda.challenge.hibernate;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class PostgreSQLDialect extends PostgreSQL94Dialect {

    public PostgreSQLDialect() {
        registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
