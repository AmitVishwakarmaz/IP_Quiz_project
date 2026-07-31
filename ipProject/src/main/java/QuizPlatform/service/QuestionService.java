package QuizPlatform.service;

import QuizPlatform.dto.QuestionDTO;
import QuizPlatform.entity.QuestionEntity;
import QuizPlatform.entity.QuizEntity;
import QuizPlatform.repository.QuestionRepository;
import QuizPlatform.repository.QuizRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ModelMapper modelMapper;

    //CREATE
    public QuestionDTO addQuestion(QuestionDTO dto) {
        QuestionEntity entity = new QuestionEntity();
        entity.setQuestionText(dto.getQuestionText());
        entity.setOptionA(dto.getOptionA());
        entity.setOptionB(dto.getOptionB());
        entity.setOptionC(dto.getOptionC());
        entity.setOptionD(dto.getOptionD());
        entity.setCorrectOption(dto.getCorrectOption());
        entity.setCategory(dto.getCategory());

        // Link quiz
        QuizEntity quiz = quizRepository.findById(dto.getQuizId()).orElse(null);
        entity.setQuiz(quiz);

        QuestionEntity saved = questionRepository.save(entity);
        dto.setId(saved.getId());
        return dto;
    }

    //READ BY QUIZ ID
    public List<QuestionDTO> getQuestionsByQuizId(Long quizId) {
        List<QuestionEntity> entities = questionRepository.findByQuizId(quizId);
        List<QuestionDTO> dtoList = new ArrayList<>();
        for (QuestionEntity entity : entities) {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(entity.getId());
            dto.setQuestionText(entity.getQuestionText());
            dto.setOptionA(entity.getOptionA());
            dto.setOptionB(entity.getOptionB());
            dto.setOptionC(entity.getOptionC());
            dto.setOptionD(entity.getOptionD());
            dto.setCorrectOption(entity.getCorrectOption());
            dto.setCategory(entity.getCategory());
            dto.setQuizId(entity.getQuiz() != null ? entity.getQuiz().getId() : null);
            dtoList.add(dto);
        }
        return dtoList;
    }

    //DELETE
    public String deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return "Question Deleted";
        }
        return "Question Not Found";
    }
}
