package com.domnerka.workflow.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;

    public Mono<String> fetchUsers(String jwtToken) {
        return webClient.get()
                .uri("admin/users")
                .headers(headers -> setHeaders(headers, jwtToken))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Failed to fetch users")))
                .bodyToMono(String.class);
    }
    public Mono<String> fetchUserById(String userId, String jwtToken) {
        return webClient.get()
                .uri("admin/users/{userId}", userId)
                .headers(headers -> setHeaders(headers, jwtToken))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Failed to fetch user by ID")))
                .bodyToMono(String.class);
    }

    private void setHeaders(HttpHeaders headers, String jwtToken) {
        headers.set(HttpHeaders.AUTHORIZATION, jwtToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }
}
