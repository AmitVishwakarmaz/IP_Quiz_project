package QuizPlatform.Controller;

import QuizPlatform.dto.ResultDTO;
import QuizPlatform.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping("/attempt/{attemptId}")
    public List<ResultDTO> getResultsByAttempt(@PathVariable Long attemptId) {
        return resultService.getResultsByAttempt(attemptId);
    }

    @GetMapping("/analytics/{quizId}")
    public Map<String, Object> getQuizAnalytics(@PathVariable Long quizId) {
        return resultService.getQuizAnalytics(quizId);
    }
}
