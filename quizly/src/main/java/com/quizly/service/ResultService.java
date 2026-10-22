package com.quizly.service;

import com.quizly.dto.ResultDTO;
import com.quizly.entity.AttemptEntity;
import com.quizly.entity.ResultEntity;
import com.quizly.repository.AttemptRepository;
import com.quizly.repository.QuizRepository;
import com.quizly.repository.ResultRepository;
import com.quizly.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuizRepository quizRepository;

    //GET RESULTS BY ATTEMPT (per-student detail)
    public List<ResultDTO> getResultsByAttempt(Long attemptId) {
        List<ResultEntity> entities = resultRepository.findByAttemptId(attemptId);
        List<ResultDTO> dtoList = new ArrayList<>();
        for (ResultEntity entity : entities) {
            ResultDTO dto = new ResultDTO();
            dto.setId(entity.getId());
            dto.setAttemptId(entity.getAttempt() != null ? entity.getAttempt().getId() : null);
            dto.setQuestionId(entity.getQuestion() != null ? entity.getQuestion().getId() : null);
            dto.setQuestionText(entity.getQuestion() != null ? entity.getQuestion().getQuestionText() : "");
            dto.setSelectedOption(entity.getSelectedOption());
            dto.setCorrectOption(entity.getCorrectOption());
            dto.setCorrect(entity.isCorrect());
            dtoList.add(dto);
        }
        return dtoList;
    }

    public List<ResultDTO> getResultsByAttemptForStudent(Long attemptId, String studentEmail) {
        AttemptEntity attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));
        var student = studentRepository.findByEmailIgnoreCase(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Student account not found"));
        if (attempt.getStudent() == null || !attempt.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own quiz results");
        }
        return getResultsByAttempt(attemptId);
    }

    public List<ResultDTO> getResultsByAttemptForInstructor(Long attemptId, String instructorEmail) {
        AttemptEntity attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));
        if (attempt.getQuiz() == null || attempt.getQuiz().getInstructor() == null
                || !attempt.getQuiz().getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view results for your own quizzes");
        }
        return getResultsByAttempt(attemptId);
    }

    //CLASS-WIDE ANALYTICS FOR A QUIZ
    public Map<String, Object> getQuizAnalytics(Long quizId) {
        List<AttemptEntity> attempts = attemptRepository.findByQuizId(quizId);
        Map<String, Object> analytics = new LinkedHashMap<>();

        if (attempts.isEmpty()) {
            analytics.put("message", "No attempts found for this quiz");
            return analytics;
        }

        int totalAttempts = attempts.size();
        int totalScore = 0;
        int maxScore = 0;
        int minScore = Integer.MAX_VALUE;
        int totalQuestions = attempts.get(0).getTotalQuestions();
        int passCount = 0;

        for (AttemptEntity attempt : attempts) {
            totalScore += attempt.getScore();
            if (attempt.getScore() > maxScore) maxScore = attempt.getScore();
            if (attempt.getScore() < minScore) minScore = attempt.getScore();
            // Pass if score >= 40%
            if (totalQuestions > 0 && (attempt.getScore() * 100.0 / totalQuestions) >= 40) {
                passCount++;
            }
        }

        double avgScore = (double) totalScore / totalAttempts;
        double passPercentage = (double) passCount * 100 / totalAttempts;

        analytics.put("quizId", quizId);
        analytics.put("totalAttempts", totalAttempts);
        analytics.put("totalQuestions", totalQuestions);
        analytics.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
        analytics.put("highestScore", maxScore);
        analytics.put("lowestScore", minScore);
        analytics.put("passPercentage", Math.round(passPercentage * 100.0) / 100.0);

        return analytics;
    }

    public Map<String, Object> getQuizAnalyticsForInstructor(Long quizId, String instructorEmail) {
        var quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        if (quiz.getInstructor() == null
                || !quiz.getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view analytics for your own quizzes");
        }
        return getQuizAnalytics(quizId);
    }
}
