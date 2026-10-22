package com.quizly.repository;

import com.quizly.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    List<QuizEntity> findByInstructorId(Long instructorId);

    List<QuizEntity> findByInstructorEmailIgnoreCase(String email);

    Optional<QuizEntity> findByIdAndInstructorEmailIgnoreCase(Long id, String email);
}
