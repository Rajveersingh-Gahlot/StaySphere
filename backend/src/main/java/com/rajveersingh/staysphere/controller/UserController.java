package com.rajveersingh.staysphere.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajveersingh.staysphere.dto.RegisterRequest;
import com.rajveersingh.staysphere.dto.RegisterResponse;
import com.rajveersingh.staysphere.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody  RegisterRequest request) {
        RegisterResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
