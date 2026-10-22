package com.quizly.service;

import com.quizly.dto.QuizDTO;
import com.quizly.entity.InstructorEntity;
import com.quizly.entity.QuizEntity;
import com.quizly.repository.AttemptRepository;
import com.quizly.repository.InstructorRepository;
import com.quizly.repository.QuestionRepository;
import com.quizly.repository.QuizRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final InstructorRepository instructorRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    public QuizService(QuizRepository quizRepository, InstructorRepository instructorRepository,
                      QuestionRepository questionRepository, AttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.instructorRepository = instructorRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
    }

    public QuizDTO addQuiz(QuizDTO dto, String instructorEmail) {
        if (dto == null || isBlank(dto.getTitle()) || isBlank(dto.getCategory())
                || dto.getTimeLimitMinutes() < 1 || dto.getTimeLimitMinutes() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A title, category, and valid time limit are required");
        }

        InstructorEntity instructor = instructorRepository.findByEmailIgnoreCase(instructorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Instructor account not found"));
        QuizEntity entity = new QuizEntity();
        entity.setTitle(dto.getTitle().trim());
        entity.setCategory(dto.getCategory().trim());
        entity.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        entity.setInstructor(instructor);
        return toDTO(quizRepository.save(entity));
    }

    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<QuizDTO> getQuizzesByInstructor(String email) {
        return quizRepository.findByInstructorEmailIgnoreCase(email).stream().map(this::toDTO).toList();
    }

    public QuizDTO getQuizById(Long id) {
        return quizRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Transactional
    public String deleteQuiz(Long id, String instructorEmail) {
        QuizEntity quiz = findOwnedQuiz(id, instructorEmail);
        if (attemptRepository.existsByQuizId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This quiz has student attempts and cannot be deleted");
        }
        questionRepository.deleteAll(questionRepository.findByQuizId(id));
        quizRepository.delete(quiz);
        return "Quiz Deleted";
    }

    public QuizEntity findOwnedQuiz(Long id, String instructorEmail) {
        QuizEntity quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        if (quiz.getInstructor() == null
                || !quiz.getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own quizzes");
        }
        return quiz;
    }

    private QuizDTO toDTO(QuizEntity entity) {
        QuizDTO dto = new QuizDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCategory(entity.getCategory());
        dto.setTimeLimitMinutes(entity.getTimeLimitMinutes());
        dto.setInstructorId(entity.getInstructor() != null ? entity.getInstructor().getId() : null);
        return dto;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
