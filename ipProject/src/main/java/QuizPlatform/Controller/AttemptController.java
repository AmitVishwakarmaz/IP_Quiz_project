package QuizPlatform.Controller;

import QuizPlatform.dto.AttemptDTO;
import QuizPlatform.dto.QuizSubmissionDTO;
import QuizPlatform.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attempts")
public class AttemptController {

    @Autowired
    private AttemptService attemptService;

    @PostMapping("/submit")
    public AttemptDTO submitQuiz(@RequestBody QuizSubmissionDTO submission) {
        return attemptService.submitQuiz(submission);
    }

    @GetMapping("/student/{name}")
    public List<AttemptDTO> getAttemptsByStudent(@PathVariable String name) {
        return attemptService.getAttemptsByStudent(name);
    }

    @GetMapping("/quiz/{quizId}")
    public List<AttemptDTO> getAttemptsByQuiz(@PathVariable Long quizId) {
        return attemptService.getAttemptsByQuiz(quizId);
    }
}
