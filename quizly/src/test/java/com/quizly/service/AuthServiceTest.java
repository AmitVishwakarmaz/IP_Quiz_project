package com.quizly.service;

import com.quizly.dto.AuthResponse;
import com.quizly.dto.InstructorRegisterRequest;
import com.quizly.dto.LoginRequest;
import com.quizly.dto.StudentRegisterRequest;
import com.quizly.entity.InstructorEntity;
import com.quizly.entity.StudentEntity;
import com.quizly.repository.InstructorRepository;
import com.quizly.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(instructorRepository, studentRepository, passwordEncoder,
                jwtService, "instructor-secret");
    }

    @Test
    void studentSignupCreatesOnlyAStudentAccount() {
        when(instructorRepository.existsByEmailIgnoreCase("student@example.com")).thenReturn(false);
        when(studentRepository.existsByEmailIgnoreCase("student@example.com")).thenReturn(false);
        when(passwordEncoder.encode("student-password")).thenReturn("bcrypt-hash");
        when(studentRepository.save(any(StudentEntity.class))).thenAnswer(invocation -> {
            StudentEntity student = invocation.getArgument(0);
            student.setId(12L);
            return student;
        });
        when(jwtService.createToken("student@example.com", "STUDENT")).thenReturn("student-token");

        AuthResponse response = authService.registerStudent(
                new StudentRegisterRequest("Student", " STUDENT@example.com ", "student-password"));

        assertEquals(12L, response.id());
        assertEquals("student@example.com", response.email());
        assertEquals("STUDENT", response.role());
        assertEquals("student-token", response.token());
        verify(studentRepository).save(argThat(student ->
                "bcrypt-hash".equals(student.getPasswordHash())
                        && "student@example.com".equals(student.getEmail())));
        verify(instructorRepository, never()).save(any());
    }

    @Test
    void instructorSignupRequiresTheInviteCode() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.registerInstructor(
                        new InstructorRegisterRequest("Teacher", "teacher@example.com",
                                "teacher-password", "wrong-code")));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(instructorRepository, studentRepository, passwordEncoder);
    }

    @Test
    void instructorSignupStoresPasswordHashWhenInviteIsValid() {
        when(studentRepository.existsByEmailIgnoreCase("teacher@example.com")).thenReturn(false);
        when(instructorRepository.findByEmailIgnoreCase("teacher@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("teacher-password")).thenReturn("bcrypt-hash");
        when(instructorRepository.save(any(InstructorEntity.class))).thenAnswer(invocation -> {
            InstructorEntity instructor = invocation.getArgument(0);
            instructor.setId(4L);
            return instructor;
        });
        when(jwtService.createToken("teacher@example.com", "INSTRUCTOR")).thenReturn("instructor-token");

        AuthResponse response = authService.registerInstructor(
                new InstructorRegisterRequest("Teacher", "teacher@example.com",
                        "teacher-password", "instructor-secret"));

        assertEquals("INSTRUCTOR", response.role());
        assertEquals("instructor-token", response.token());
        verify(instructorRepository).save(argThat(instructor ->
                "bcrypt-hash".equals(instructor.getPasswordHash())));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void studentCannotRegisterUsingAnInstructorAccountEmail() {
        when(instructorRepository.existsByEmailIgnoreCase("teacher@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.registerStudent(new StudentRegisterRequest(
                        "Student", "teacher@example.com", "student-password")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void loginUsesOnlyTheSelectedAccountType() {
        when(studentRepository.findByEmailIgnoreCase("teacher@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.loginStudent(new LoginRequest("teacher@example.com", "password")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(instructorRepository, never()).findByEmailIgnoreCase(any());
    }
}
