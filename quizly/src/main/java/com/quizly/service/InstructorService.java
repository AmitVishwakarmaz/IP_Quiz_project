package com.quizly.service;

import com.quizly.dto.InstructorDTO;
import com.quizly.entity.InstructorEntity;
import com.quizly.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<InstructorDTO> getAllInstructors() {
        return instructorRepository.findAll().stream().map(this::toDTO).toList();
    }

    public InstructorDTO getInstructorById(Long id) {
        return instructorRepository.findById(id).map(this::toDTO).orElse(null);
    }

    private InstructorDTO toDTO(InstructorEntity instructor) {
        return new InstructorDTO(instructor.getId(), instructor.getName(), instructor.getEmail());
    }
}
