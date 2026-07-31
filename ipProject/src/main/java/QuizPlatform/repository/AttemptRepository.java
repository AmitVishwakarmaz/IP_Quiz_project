package QuizPlatform.repository;

import QuizPlatform.entity.AttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<AttemptEntity, Long> {
    List<AttemptEntity> findByQuizId(Long quizId);
    List<AttemptEntity> findByStudentName(String studentName);
}
