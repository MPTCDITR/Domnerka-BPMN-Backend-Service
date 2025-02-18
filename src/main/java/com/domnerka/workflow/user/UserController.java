package com.domnerka.workflow.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public Mono<ResponseEntity<String>> getUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        return userService.fetchUsers(jwtToken)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<String>> getUserById(
            @PathVariable String userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        return userService.fetchUserById(userId, jwtToken)
                .map(ResponseEntity::ok);
    }

}
