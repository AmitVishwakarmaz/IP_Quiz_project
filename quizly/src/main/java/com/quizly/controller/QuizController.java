package com.quizly.controller;

import com.quizly.dto.QuizDTO;
import com.quizly.service.QuizService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public QuizDTO addQuiz(@RequestBody QuizDTO dto, Authentication authentication) {
        return quizService.addQuiz(dto, authentication.getName());
    }

    @GetMapping
    public List<QuizDTO> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/mine")
    public List<QuizDTO> getMyQuizzes(Authentication authentication) {
        return quizService.getQuizzesByInstructor(authentication.getName());
    }

    @GetMapping("/{id}")
    public QuizDTO getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteQuiz(@PathVariable Long id, Authentication authentication) {
        return quizService.deleteQuiz(id, authentication.getName());
    }
}
