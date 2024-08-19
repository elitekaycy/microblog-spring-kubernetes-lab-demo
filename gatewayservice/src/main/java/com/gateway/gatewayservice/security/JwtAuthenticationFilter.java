package com.gateway.gatewayservice.security;

import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

  private final RestTemplate restTemplate;

  public JwtAuthenticationFilter(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
      return Mono.error(new RuntimeException("Missing authorization header"));
    }

    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    String jwtToken = authHeader.replace("Bearer ", "");

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<Object> response = restTemplate.exchange(
          "http://user-service/auth/me",
          HttpMethod.GET,
          entity,
          Object.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof Map) {

        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) response.getBody();

        // Extract user details from the map
        String username = (String) userMap.get("username");
        String userRole = (String) userMap.get("roles");

        // Add user details to the request headers
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Name", username)
            .header("X-User-Roles", userRole)
            .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
      } else {
        return Mono.error(new RuntimeException("Unauthorized"));
      }
    } catch (Exception e) {
      return Mono.error(new RuntimeException("Unauthorized"));
    }
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
