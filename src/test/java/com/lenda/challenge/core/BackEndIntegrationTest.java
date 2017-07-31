package com.lenda.challenge.core;

import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
@SpringBootTest
@ActiveProfiles(value = "local")
@TestExecutionListeners(
        listeners = {FlywayTestExecutionListener.class},
        mergeMode = MERGE_WITH_DEFAULTS
)
public @interface BackEndIntegrationTest {
}
