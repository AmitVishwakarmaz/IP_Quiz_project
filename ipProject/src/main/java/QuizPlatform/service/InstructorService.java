package QuizPlatform.service;

import QuizPlatform.dto.InstructorDTO;
import QuizPlatform.entity.InstructorEntity;
import QuizPlatform.repository.InstructorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private ModelMapper modelMapper;

    //CREATE
    public InstructorDTO addInstructor(InstructorDTO dto) {
        InstructorEntity entity = modelMapper.map(dto, InstructorEntity.class);
        InstructorEntity saved = instructorRepository.save(entity);
        return modelMapper.map(saved, InstructorDTO.class);
    }

    //READ ALL
    public List<InstructorDTO> getAllInstructors() {
        List<InstructorEntity> entities = instructorRepository.findAll();
        List<InstructorDTO> dtoList = new ArrayList<>();
        for (InstructorEntity entity : entities) {
            dtoList.add(modelMapper.map(entity, InstructorDTO.class));
        }
        return dtoList;
    }

    //READ BY ID
    public InstructorDTO getInstructorById(Long id) {
        InstructorEntity entity = instructorRepository.findById(id).orElse(null);
        if (entity == null) return null;
        return modelMapper.map(entity, InstructorDTO.class);
    }

    //DELETE
    public String deleteInstructor(Long id) {
        if (instructorRepository.existsById(id)) {
            instructorRepository.deleteById(id);
            return "Instructor Deleted";
        }
        return "Instructor Not Found";
    }
}
