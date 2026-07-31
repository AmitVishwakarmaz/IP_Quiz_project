package QuizPlatform.Controller;

import QuizPlatform.dto.InstructorDTO;
import QuizPlatform.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @PostMapping
    public InstructorDTO addInstructor(@RequestBody InstructorDTO dto) {
        return instructorService.addInstructor(dto);
    }

    @GetMapping
    public List<InstructorDTO> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @GetMapping("/{id}")
    public InstructorDTO getInstructorById(@PathVariable Long id) {
        return instructorService.getInstructorById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteInstructor(@PathVariable Long id) {
        return instructorService.deleteInstructor(id);
    }
}
