package com.banco.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${microservicio.cliente.url}")
    private String clienteUrl;
    
    @Value("${microservicio.producto.url}")
    private String productoUrl;
    
    @Value("${auth.server.url}")
    private String authServerUrl;
    
    @Bean
    public WebClient clienteWebClient() {
        return WebClient.builder()
                .baseUrl(clienteUrl)
                .build();
    }
    
    @Bean
    public WebClient productoWebClient() {
        return WebClient.builder()
                .baseUrl(productoUrl)
                .build();
    }
    
    @Bean
    public WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl(authServerUrl)
                .build();
    }
}