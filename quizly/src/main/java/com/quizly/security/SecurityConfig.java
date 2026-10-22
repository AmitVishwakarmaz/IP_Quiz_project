package com.quizly.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"message\":\"Sign in to access this feature\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"message\":\"You are not allowed to access this feature\"}");
                        }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/error",
                                "/auth/instructor/register", "/auth/instructor/login",
                                "/auth/student/register", "/auth/student/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/quizzes/mine",
                                "/questions/quiz/*/answer-key", "/attempts/student/**",
                                "/attempts/quiz/**", "/attempts", "/results/analytics/**", "/instructors/**")
                                .hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET, "/quizzes", "/quizzes/**",
                                "/questions/quiz/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/attempts/submit").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/attempts/me").hasRole("STUDENT")
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/attempts/**", "/results/**").authenticated()
                        .anyRequest().hasRole("INSTRUCTOR"))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
