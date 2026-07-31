package QuizPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    private String studentName;
    private Long quizId;
    // Map of questionId -> selectedOption (A/B/C/D)
    private Map<Long, String> answers;
}
