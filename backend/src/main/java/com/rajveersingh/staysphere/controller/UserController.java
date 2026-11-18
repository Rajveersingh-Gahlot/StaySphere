package com.rajveersingh.staysphere.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.LoginRequest;
import com.rajveersingh.staysphere.dto.LoginResponse;
import com.rajveersingh.staysphere.dto.LoginResult;
import com.rajveersingh.staysphere.dto.RegisterRequest;
import com.rajveersingh.staysphere.dto.RegisterResponse;
import com.rajveersingh.staysphere.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResult result = service.login(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", result.token())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(jwtExpirationMs))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.response());
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {

        return Map.of("token", csrfToken.getToken());
    }

}
