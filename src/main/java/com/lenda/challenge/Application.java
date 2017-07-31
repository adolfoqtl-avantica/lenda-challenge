package com.lenda.challenge;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot Application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lenda.challenge.repository.postgres")
@EnableJpaAuditing
@EnableMongoRepositories(basePackages = "com.lenda.challenge.repository.mongo")
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableEncryptableProperties
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching(mode = AdviceMode.ASPECTJ)
@ComponentScan(basePackages = "com.lenda.challenge")
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
