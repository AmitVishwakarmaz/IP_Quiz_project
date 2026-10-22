package com.quizly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDTO {
    private Long id;
    private String studentName;
    private Long quizId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
    private int totalQuestions;
}
