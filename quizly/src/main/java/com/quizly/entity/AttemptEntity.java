package com.quizly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
    private int totalQuestions;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;
}
