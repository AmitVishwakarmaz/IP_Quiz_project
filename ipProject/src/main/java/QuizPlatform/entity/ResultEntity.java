package QuizPlatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String selectedOption;
    private String correctOption;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private AttemptEntity attempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;
}
