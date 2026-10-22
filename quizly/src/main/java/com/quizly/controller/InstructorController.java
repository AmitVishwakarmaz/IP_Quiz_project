package com.quizly.controller;

import com.quizly.dto.InstructorDTO;
import com.quizly.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @GetMapping
    public List<InstructorDTO> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @GetMapping("/{id}")
    public InstructorDTO getInstructorById(@PathVariable Long id) {
        return instructorService.getInstructorById(id);
    }

}
