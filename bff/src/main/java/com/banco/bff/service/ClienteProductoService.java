package com.banco.bff.service;

import com.banco.bff.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteProductoService {

    private final WebClient clienteWebClient;
    private final WebClient productoWebClient;
    private final OAuth2ClientService oAuth2ClientService;

    public Mono<ClienteProductoResponse> obtenerClienteConProductos(String codigoUnico, String traceId) {
        log.info("BFF - Consulta iniciada - TraceId: {}", traceId);

        return obtenerTokenServicio()
            .flatMap(token -> {
                Mono<JsonNode> clienteMono = llamarClienteService(codigoUnico, traceId, token);
                Mono<List<ProductoDTO>> productosMono = llamarProductoService(codigoUnico, traceId, token);

                return Mono.zip(clienteMono, productosMono)
                    .map(tuple -> construirRespuesta(tuple.getT1(), tuple.getT2(), traceId))
                    .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado")));
            });
    }

    private Mono<String> obtenerTokenServicio() {
        return oAuth2ClientService.getAccessToken()
            .doOnSuccess(token -> log.info("Token obtenido exitosamente"))
            .doOnError(e -> log.error("Error obteniendo token: {}", e.getMessage()));
    }

    private Mono<JsonNode> llamarClienteService(String codigo, String traceId, String token) {
        ClienteRequest request = new ClienteRequest(codigo, traceId);

        return clienteWebClient.post()
            .uri("/v1/clientes/buscar")
            .header("Authorization", "Bearer " + token)
            .header("X-Trace-Id", traceId)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .doOnSuccess(c -> log.info("Cliente obtenido"))
            .doOnError(e -> log.error("Error cliente: {}", e.getMessage()))
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(e -> Mono.empty());
    }

    private Mono<List<ProductoDTO>> llamarProductoService(String codigo, String traceId, String token) {
        ProductoRequest request = new ProductoRequest();
        request.setCodigoUnicoCliente(codigo);
        request.setTraceId(traceId);
        log.info("Enviando a ms-producto: codigoUnicoCliente='{}', traceId='{}'", request.getCodigoUnicoCliente(), request.getTraceId());
        return productoWebClient.post()
            .uri("/v1/productos/buscar")
            .header("Authorization", "Bearer " + token)
            .header("X-Trace-Id", traceId)
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(ProductoDTO.class)
            .collectList()
            .doOnSuccess(p -> log.info("Productos obtenidos: {}", p.size()))
            .doOnError(e -> log.error("Error productos: {}", e.getMessage()))
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(e -> Mono.just(Collections.emptyList()));
    }

    private ClienteProductoResponse construirRespuesta(JsonNode clienteJson, List<ProductoDTO> productos, String traceId) {
        if (clienteJson == null || clienteJson.isNull()) {
            throw new RuntimeException("Cliente no encontrado");
        }

        productos.forEach(p -> p.setTraceId(traceId));

        return ClienteProductoResponse.builder()
                .nombres(clienteJson.get("nombres").asText())
                .apellidos(clienteJson.get("apellidos").asText())
                .tipoDocumento(clienteJson.get("tipoDocumento").asText())
                .numeroDocumento(clienteJson.get("numeroDocumento").asText())
                .productos(productos)
                .traceId(traceId)
                .build();
    }
}
