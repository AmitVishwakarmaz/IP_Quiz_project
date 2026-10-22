package com.quizly.controller;

import com.quizly.dto.AttemptDTO;
import com.quizly.dto.QuizSubmissionDTO;
import com.quizly.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attempts")
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    @PostMapping("/submit")
    public AttemptDTO submitQuiz(@RequestBody QuizSubmissionDTO submission, Authentication authentication) {
        return attemptService.submitQuiz(submission, authentication.getName());
    }

    @GetMapping("/me")
    public List<AttemptDTO> getMyAttempts(Authentication authentication) {
        return attemptService.getMyAttempts(authentication.getName());
    }

    @GetMapping
    public List<AttemptDTO> getAllAttempts() {
        return attemptService.getAllAttempts();
    }

    @GetMapping("/student/{name}")
    public List<AttemptDTO> getAttemptsByStudent(@PathVariable String name) {
        return attemptService.getAttemptsByStudent(name);
    }

    @GetMapping("/quiz/{quizId}")
    public List<AttemptDTO> getAttemptsByQuiz(@PathVariable Long quizId) {
        return attemptService.getAttemptsByQuiz(quizId);
    }
}
