package com.agro.trace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
public class AgroTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroTraceApplication.class, args);
    }
}