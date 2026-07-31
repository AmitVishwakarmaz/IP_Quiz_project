package QuizPlatform.service;

import QuizPlatform.dto.AttemptDTO;
import QuizPlatform.dto.QuizSubmissionDTO;
import QuizPlatform.entity.*;
import QuizPlatform.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ModelMapper modelMapper;

    //SUBMIT QUIZ - Auto-grade MCQs and save results
    public AttemptDTO submitQuiz(QuizSubmissionDTO submission) {
        QuizEntity quiz = quizRepository.findById(submission.getQuizId()).orElse(null);
        if (quiz == null) return null;

        List<QuestionEntity> questions = questionRepository.findByQuizId(submission.getQuizId());

        // Create attempt
        AttemptEntity attempt = new AttemptEntity();
        attempt.setStudentName(submission.getStudentName());
        attempt.setQuiz(quiz);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setTotalQuestions(questions.size());

        // Auto-grade
        int score = 0;
        AttemptEntity savedAttempt = attemptRepository.save(attempt);

        for (QuestionEntity question : questions) {
            String selectedOption = submission.getAnswers().get(question.getId());
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
    public List<AttemptDTO> getAttemptsByStudent(String studentName) {
        List<AttemptEntity> entities = attemptRepository.findByStudentName(studentName);
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
    public List<AttemptDTO> getAttemptsByQuiz(Long quizId) {
        List<AttemptEntity> entities = attemptRepository.findByQuizId(quizId);
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
}
