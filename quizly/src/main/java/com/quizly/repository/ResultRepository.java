package com.quizly.repository;

import com.quizly.entity.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
    List<ResultEntity> findByAttemptId(Long attemptId);

    boolean existsByQuestionId(Long questionId);
}
