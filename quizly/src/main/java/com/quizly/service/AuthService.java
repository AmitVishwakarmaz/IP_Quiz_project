package com.quizly.service;

import com.quizly.dto.AccountDTO;
import com.quizly.dto.AuthResponse;
import com.quizly.dto.InstructorRegisterRequest;
import com.quizly.dto.LoginRequest;
import com.quizly.dto.StudentRegisterRequest;
import com.quizly.entity.InstructorEntity;
import com.quizly.entity.StudentEntity;
import com.quizly.repository.InstructorRepository;
import com.quizly.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
public class AuthService {

    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String instructorInviteCode;

    public AuthService(InstructorRepository instructorRepository, StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       @Value("${app.auth.instructor-invite-code:}") String instructorInviteCode) {
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.instructorInviteCode = instructorInviteCode;
    }

    public AuthResponse registerInstructor(InstructorRegisterRequest request) {
        if (request == null || !hasValidInstructorInvite(request.inviteCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A valid instructor invite code is required");
        }
        String email = validateRegistration(request.name(), request.email(), request.password());
        ensureEmailAvailableForNewRole(email, true);

        InstructorEntity instructor = instructorRepository.findByEmailIgnoreCase(email).orElseGet(InstructorEntity::new);
        if (instructor.getPasswordHash() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An instructor account with this email already exists");
        }
        instructor.setName(request.name().trim());
        instructor.setEmail(email);
        instructor.setPasswordHash(passwordEncoder.encode(request.password()));
        return instructorResponse(instructorRepository.save(instructor));
    }

    public AuthResponse registerStudent(StudentRegisterRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, email, and password are required");
        }
        String email = validateRegistration(request.name(), request.email(), request.password());
        ensureEmailAvailableForNewRole(email, false);
        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A student account with this email already exists");
        }
        StudentEntity student = new StudentEntity();
        student.setName(request.name().trim());
        student.setEmail(email);
        student.setPasswordHash(passwordEncoder.encode(request.password()));
        return studentResponse(studentRepository.save(student));
    }

    public AuthResponse loginInstructor(LoginRequest request) {
        if (request == null || isBlank(request.email()) || request.password() == null) {
            throw invalidCredentials();
        }
        InstructorEntity instructor = instructorRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .filter(account -> account.getPasswordHash() != null
                        && passwordEncoder.matches(request.password(), account.getPasswordHash()))
                .orElseThrow(AuthService::invalidCredentials);
        return instructorResponse(instructor);
    }

    public AuthResponse loginStudent(LoginRequest request) {
        if (request == null || isBlank(request.email()) || request.password() == null) {
            throw invalidCredentials();
        }
        StudentEntity student = studentRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .filter(account -> passwordEncoder.matches(request.password(), account.getPasswordHash()))
                .orElseThrow(AuthService::invalidCredentials);
        return studentResponse(student);
    }

    public AccountDTO getCurrentAccount(String email, boolean instructor) {
        if (instructor) {
            InstructorEntity account = instructorRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
            return new AccountDTO(account.getId(), account.getName(), account.getEmail(), "INSTRUCTOR");
        }
        StudentEntity account = studentRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));
        return new AccountDTO(account.getId(), account.getName(), account.getEmail(), "STUDENT");
    }

    private String validateRegistration(String name, String email, String password) {
        if (isBlank(name) || isBlank(email) || password == null || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name, email, and a password of 8 to 72 bytes are required");
        }
        String normalizedEmail = normalizeEmail(email);
        if (!normalizedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid email address");
        }
        return normalizedEmail;
    }

    private void ensureEmailAvailableForNewRole(String email, boolean instructor) {
        if (instructor ? studentRepository.existsByEmailIgnoreCase(email)
                : instructorRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered for another account type");
        }
    }

    private boolean hasValidInstructorInvite(String suppliedCode) {
        if (isBlank(instructorInviteCode) || isBlank(suppliedCode)) {
            return false;
        }
        return MessageDigest.isEqual(instructorInviteCode.getBytes(StandardCharsets.UTF_8),
                suppliedCode.getBytes(StandardCharsets.UTF_8));
    }

    private AuthResponse instructorResponse(InstructorEntity account) {
        return new AuthResponse(jwtService.createToken(account.getEmail(), "INSTRUCTOR"),
                account.getId(), account.getName(), account.getEmail(), "INSTRUCTOR");
    }

    private AuthResponse studentResponse(StudentEntity account) {
        return new AuthResponse(jwtService.createToken(account.getEmail(), "STUDENT"),
                account.getId(), account.getName(), account.getEmail(), "STUDENT");
    }

    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
