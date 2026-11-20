
package com.rajveersingh.staysphere.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

                csrfRepository.setCookiePath("/");
                csrfRepository.setCookieCustomizer(
                                cookie -> cookie.sameSite("Lax"));

                CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();

                http
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(csrfRepository)
                                                .csrfTokenRequestHandler(csrfHandler)
                                                .sessionAuthenticationStrategy(
                                                                new NullAuthenticatedSessionStrategy()))

                                .cors(cors -> cors
                                                .configurationSource(corsConfigurationSource()))

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/users/register",
                                                                "/api/users/login")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/users/csrf")
                                                .permitAll()

                                                .requestMatchers("/api/admin/**")
                                                .hasRole("SUPER_ADMIN")

                                                .requestMatchers("/api/owner/**")
                                                .hasRole("PG_OWNER")

                                                .requestMatchers("/api/tenant/**")
                                                .hasRole("TENANT")

                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(
                                                                (request, response, exception) -> response.sendError(
                                                                                HttpServletResponse.SC_UNAUTHORIZED))
                                                .accessDeniedHandler(
                                                                (request, response, exception) -> response.sendError(
                                                                                HttpServletResponse.SC_FORBIDDEN)))

                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())

                                .logout(logout -> logout
                                                .logoutUrl("/api/users/logout")

                                                .addLogoutHandler(
                                                                (request, response, authentication) -> {

                                                                        ResponseCookie cookie = ResponseCookie
                                                                                        .from("access_token", "")
                                                                                        .httpOnly(true)
                                                                                        .secure(false)
                                                                                        .sameSite("Lax")
                                                                                        .path("/")
                                                                                        .maxAge(0)
                                                                                        .build();

                                                                        response.addHeader(
                                                                                        HttpHeaders.SET_COOKIE,
                                                                                        cookie.toString());
                                                                })

                                                .logoutSuccessHandler(
                                                                new HttpStatusReturningLogoutSuccessHandler(
                                                                                HttpStatus.NO_CONTENT)))

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(
                                List.of("http://localhost:5173"));

                config.setAllowedMethods(
                                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                config.setAllowedHeaders(
                                List.of("Content-Type", "X-XSRF-TOKEN"));

                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);

                return source;
        }
}
