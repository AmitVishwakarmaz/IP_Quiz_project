package com.quizly.service;

import com.quizly.dto.AttemptDTO;
import com.quizly.dto.QuizSubmissionDTO;
import com.quizly.entity.*;
import com.quizly.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AttemptService {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    //SUBMIT QUIZ - Auto-grade MCQs and save results
    @Transactional
    public AttemptDTO submitQuiz(QuizSubmissionDTO submission, String studentEmail) {
        if (submission == null || submission.getQuizId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quiz must be selected");
        }
        StudentEntity student = studentRepository.findByEmailIgnoreCase(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Student account not found"));
        QuizEntity quiz = quizRepository.findById(submission.getQuizId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<QuestionEntity> questions = questionRepository.findByQuizId(submission.getQuizId());
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This quiz does not contain any questions");
        }

        AttemptEntity attempt = new AttemptEntity();
        attempt.setStudentName(student.getName());
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setTotalQuestions(questions.size());

        // Auto-grade
        int score = 0;
        AttemptEntity savedAttempt = attemptRepository.save(attempt);

        for (QuestionEntity question : questions) {
            String selectedOption = submission.getAnswers() == null
                    ? null : submission.getAnswers().get(question.getId());
            boolean isCorrect = question.getCorrectOption().equalsIgnoreCase(
                    selectedOption != null ? selectedOption : "");

            if (isCorrect) score++;

            // Save individual result
            ResultEntity result = new ResultEntity();
            result.setAttempt(savedAttempt);
            result.setQuestion(question);
            result.setSelectedOption(selectedOption != null ? selectedOption : "Not Answered");
            result.setCorrectOption(question.getCorrectOption());
            result.setCorrect(isCorrect);
            resultRepository.save(result);
        }

        // Update attempt with score and end time
        savedAttempt.setScore(score);
        savedAttempt.setEndTime(LocalDateTime.now());
        attemptRepository.save(savedAttempt);

        // Return DTO
        AttemptDTO dto = new AttemptDTO();
        dto.setId(savedAttempt.getId());
        dto.setStudentName(savedAttempt.getStudentName());
        dto.setQuizId(quiz.getId());
        dto.setStartTime(savedAttempt.getStartTime());
        dto.setEndTime(savedAttempt.getEndTime());
        dto.setScore(score);
        dto.setTotalQuestions(questions.size());
        return dto;
    }

    //GET ATTEMPTS BY STUDENT
    public List<AttemptDTO> getMyAttempts(String studentEmail) {
        StudentEntity student = studentRepository.findByEmailIgnoreCase(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Student account not found"));
        return toDTOs(attemptRepository.findByStudentId(student.getId()));
    }

    public List<AttemptDTO> getAttemptsByStudent(String studentName, String instructorEmail) {
        List<AttemptEntity> entities = attemptRepository.findByStudentName(studentName).stream()
                .filter(attempt -> attempt.getQuiz() != null
                        && attempt.getQuiz().getInstructor() != null
                        && attempt.getQuiz().getInstructor().getEmail().equalsIgnoreCase(instructorEmail))
                .toList();
        return toDTOs(entities);
    }

    private List<AttemptDTO> toDTOs(List<AttemptEntity> entities) {
        List<AttemptDTO> dtoList = new ArrayList<>();
        for (AttemptEntity entity : entities) {
            AttemptDTO dto = new AttemptDTO();
            dto.setId(entity.getId());
            dto.setStudentName(entity.getStudentName());
            dto.setQuizId(entity.getQuiz() != null ? entity.getQuiz().getId() : null);
            dto.setStartTime(entity.getStartTime());
            dto.setEndTime(entity.getEndTime());
            dto.setScore(entity.getScore());
            dto.setTotalQuestions(entity.getTotalQuestions());
            dtoList.add(dto);
        }
        return dtoList;
    }

    //GET ATTEMPTS BY QUIZ
    public List<AttemptDTO> getAttemptsByQuiz(Long quizId, String instructorEmail) {
        quizRepository.findByIdAndInstructorEmailIgnoreCase(quizId, instructorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        List<AttemptEntity> entities = attemptRepository.findByQuizId(quizId);
        return toDTOs(entities);
    }
}
