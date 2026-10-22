package com.quizly.controller;

import com.quizly.dto.AccountDTO;
import com.quizly.dto.AuthResponse;
import com.quizly.dto.InstructorRegisterRequest;
import com.quizly.dto.LoginRequest;
import com.quizly.dto.StudentRegisterRequest;
import com.quizly.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/instructor/register")
    public AuthResponse registerInstructor(@RequestBody InstructorRegisterRequest request) {
        return authService.registerInstructor(request);
    }

    @PostMapping("/instructor/login")
    public AuthResponse loginInstructor(@RequestBody LoginRequest request) {
        return authService.loginInstructor(request);
    }

    @PostMapping("/student/register")
    public AuthResponse registerStudent(@RequestBody StudentRegisterRequest request) {
        return authService.registerStudent(request);
    }

    @PostMapping("/student/login")
    public AuthResponse loginStudent(@RequestBody LoginRequest request) {
        return authService.loginStudent(request);
    }

    @GetMapping("/me")
    public AccountDTO currentAccount(Authentication authentication) {
        boolean instructor = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_INSTRUCTOR"));
        return authService.getCurrentAccount(authentication.getName(), instructor);
    }
}
