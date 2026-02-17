package com.banco.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(BancoStarterProperties.class)
public class BancoStarterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BancoStarterAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix = "banco.starter", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ApplicationRunner bancoStarterRunner(BancoStarterProperties properties) {
        return args -> log.info("{}", properties.getMessage());
    }
}
