package com.quizly.controller;

import com.quizly.dto.ResultDTO;
import com.quizly.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping("/attempt/{attemptId}")
    public List<ResultDTO> getResultsByAttempt(@PathVariable Long attemptId, Authentication authentication) {
        boolean instructor = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_INSTRUCTOR"));
        return instructor
                ? resultService.getResultsByAttemptForInstructor(attemptId, authentication.getName())
                : resultService.getResultsByAttemptForStudent(attemptId, authentication.getName());
    }

    @GetMapping("/analytics/{quizId}")
    public Map<String, Object> getQuizAnalytics(@PathVariable Long quizId, Authentication authentication) {
        return resultService.getQuizAnalyticsForInstructor(quizId, authentication.getName());
    }
}
