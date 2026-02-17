package com.banco.bff.service;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class OAuth2ClientService {

    @Value("${bff.client-id}")
    private String clientId;

    @Value("${bff.client-secret}")
    private String clientSecret;

    @Value("${bff.token-uri}")
    private String tokenUri;

    private final WebClient webClient = WebClient.create();

    public Mono<String> getAccessToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "internal");

        String basicAuth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        return webClient.post()
            .uri(tokenUri)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
            .body(BodyInserters.fromFormData(body))
            .retrieve()
            .bodyToMono(String.class)
            .map(tokenResponse -> JsonPath.read(tokenResponse, "$.access_token"));
    }
}
