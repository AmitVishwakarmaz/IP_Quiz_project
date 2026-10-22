package com.quizly.service;

import com.quizly.dto.QuestionDTO;
import com.quizly.entity.QuestionEntity;
import com.quizly.entity.QuizEntity;
import com.quizly.repository.QuestionRepository;
import com.quizly.repository.QuizRepository;
import com.quizly.repository.ResultRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final ResultRepository resultRepository;

    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository,
                           ResultRepository resultRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.resultRepository = resultRepository;
    }

    public QuestionDTO addQuestion(QuestionDTO dto, String instructorEmail) {
        if (dto == null || isBlank(dto.getQuestionText()) || isBlank(dto.getOptionA())
                || isBlank(dto.getOptionB()) || isBlank(dto.getOptionC()) || isBlank(dto.getOptionD())
                || dto.getCorrectOption() == null
                || !dto.getCorrectOption().matches("(?i)[ABCD]")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question text, four options, and a valid answer are required");
        }
        QuizEntity quiz = findOwnedQuiz(dto.getQuizId(), instructorEmail);
        QuestionEntity entity = new QuestionEntity();
        entity.setQuestionText(dto.getQuestionText().trim());
        entity.setOptionA(dto.getOptionA().trim());
        entity.setOptionB(dto.getOptionB().trim());
        entity.setOptionC(dto.getOptionC().trim());
        entity.setOptionD(dto.getOptionD().trim());
        entity.setCorrectOption(dto.getCorrectOption().toUpperCase());
        entity.setCategory(dto.getCategory());
        entity.setQuiz(quiz);
        return toDTO(questionRepository.save(entity));
    }

    public List<QuestionDTO> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId).stream().map(this::toDTO).toList();
    }

    public List<QuestionDTO> getAnswerKey(Long quizId, String instructorEmail) {
        findOwnedQuiz(quizId, instructorEmail);
        return getQuestionsByQuizId(quizId);
    }

    @Transactional
    public String deleteQuestion(Long id, String instructorEmail) {
        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
        if (question.getQuiz() == null || question.getQuiz().getInstructor() == null
                || !question.getQuiz().getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage questions in your own quizzes");
        }
        if (resultRepository.existsByQuestionId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This question has student answers and cannot be deleted");
        }
        questionRepository.delete(question);
        return "Question Deleted";
    }

    private QuizEntity findOwnedQuiz(Long quizId, String instructorEmail) {
        if (quizId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quiz must be selected");
        }
        QuizEntity quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        if (quiz.getInstructor() == null
                || !quiz.getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own quizzes");
        }
        return quiz;
    }

    private QuestionDTO toDTO(QuestionEntity entity) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(entity.getId());
        dto.setQuestionText(entity.getQuestionText());
        dto.setOptionA(entity.getOptionA());
        dto.setOptionB(entity.getOptionB());
        dto.setOptionC(entity.getOptionC());
        dto.setOptionD(entity.getOptionD());
        dto.setCorrectOption(entity.getCorrectOption());
        dto.setCategory(entity.getCategory());
        dto.setQuizId(entity.getQuiz() != null ? entity.getQuiz().getId() : null);
        return dto;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
