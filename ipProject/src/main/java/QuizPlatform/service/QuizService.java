package QuizPlatform.service;

import QuizPlatform.dto.QuizDTO;
import QuizPlatform.entity.InstructorEntity;
import QuizPlatform.entity.QuizEntity;
import QuizPlatform.repository.InstructorRepository;
import QuizPlatform.repository.QuizRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private ModelMapper modelMapper;

    //CREATE
    public QuizDTO addQuiz(QuizDTO dto) {
        QuizEntity entity = new QuizEntity();
        entity.setTitle(dto.getTitle());
        entity.setCategory(dto.getCategory());
        entity.setTimeLimitMinutes(dto.getTimeLimitMinutes());

        // Link instructor
        InstructorEntity instructor = instructorRepository.findById(dto.getInstructorId()).orElse(null);
        entity.setInstructor(instructor);

        QuizEntity saved = quizRepository.save(entity);

        dto.setId(saved.getId());
        return dto;
    }

    //READ ALL
    public List<QuizDTO> getAllQuizzes() {
        List<QuizEntity> entities = quizRepository.findAll();
        List<QuizDTO> dtoList = new ArrayList<>();
        for (QuizEntity entity : entities) {
            QuizDTO dto = new QuizDTO();
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setCategory(entity.getCategory());
            dto.setTimeLimitMinutes(entity.getTimeLimitMinutes());
            dto.setInstructorId(entity.getInstructor() != null ? entity.getInstructor().getId() : null);
            dtoList.add(dto);
        }
        return dtoList;
    }

    //READ BY ID
    public QuizDTO getQuizById(Long id) {
        QuizEntity entity = quizRepository.findById(id).orElse(null);
        if (entity == null) return null;
        QuizDTO dto = new QuizDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCategory(entity.getCategory());
        dto.setTimeLimitMinutes(entity.getTimeLimitMinutes());
        dto.setInstructorId(entity.getInstructor() != null ? entity.getInstructor().getId() : null);
        return dto;
    }

    //DELETE
    public String deleteQuiz(Long id) {
        if (quizRepository.existsById(id)) {
            quizRepository.deleteById(id);
            return "Quiz Deleted";
        }
        return "Quiz Not Found";
    }
}
