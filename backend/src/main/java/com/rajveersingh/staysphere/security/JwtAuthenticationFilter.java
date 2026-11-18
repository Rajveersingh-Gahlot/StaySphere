package com.rajveersingh.staysphere.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rajveersingh.staysphere.entity.User;
import com.rajveersingh.staysphere.enums.AccountStatus;
import com.rajveersingh.staysphere.enums.ApprovalStatus;
import com.rajveersingh.staysphere.enums.Role;
import com.rajveersingh.staysphere.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UUID userId = jwtService.extractUserId(token);

                repository.findById(userId).ifPresent(user -> {
                    if (isAccountAllowed(user)) {
                        UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(user.getId().toString())
                                .password(user.getPassword())
                                .authorities(new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()))
                                .build();

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                });
            } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
                // Invalid or expired JWT: remain unauthenticated.
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private boolean isAccountAllowed(User user) {

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            return false;
        }

        return user.getRole() != Role.PG_OWNER
                || user.getApprovalStatus() == ApprovalStatus.APPROVED;
    }

}
