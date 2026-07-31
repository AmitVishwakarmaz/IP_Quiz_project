package QuizPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDTO {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private String questionText;
    private String selectedOption;
    private String correctOption;
    private boolean isCorrect;
}
