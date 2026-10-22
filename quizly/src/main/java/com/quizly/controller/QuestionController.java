package com.quizly.controller;

import com.quizly.dto.QuestionDTO;
import com.quizly.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public QuestionDTO addQuestion(@RequestBody QuestionDTO dto, Authentication authentication) {
        return questionService.addQuestion(dto, authentication.getName());
    }

    @GetMapping("/quiz/{quizId}")
    public List<QuestionDTO> getQuestionsByQuiz(@PathVariable Long quizId) {
        List<QuestionDTO> questions = questionService.getQuestionsByQuizId(quizId);
        questions.forEach(question -> question.setCorrectOption(null));
        return questions;
    }

    @GetMapping("/quiz/{quizId}/answer-key")
    public List<QuestionDTO> getQuestionAnswerKey(@PathVariable Long quizId) {
        return questionService.getAnswerKey(quizId);
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@PathVariable Long id, Authentication authentication) {
        return questionService.deleteQuestion(id, authentication.getName());
    }
}
