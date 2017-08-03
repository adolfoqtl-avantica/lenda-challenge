package com.lenda.challenge.spring;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JpaTestConfiguration {

    @Bean(name = "dataSource")
    public JdbcDataSource createH2XADataSource() {
        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setDescription("H2 datasource");
        h2DataSource.setUser("sa");
        h2DataSource.setPassword("");
        h2DataSource.setURL("jdbc:h2:mem:challenge.db;DATABASE_TO_UPPER=false;TRACE_LEVEL_SYSTEM_OUT=1;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE DOMAIN IF NOT EXISTS TIMESTAMPTZ AS TIMESTAMP \\; CREATE DOMAIN IF NOT EXISTS JSONB AS CLOB");

        return h2DataSource;
    }
}
