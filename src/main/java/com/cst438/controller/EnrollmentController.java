package com.cst438.controller;


import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // instructor downloads student enrollments for a section, ordered by student name
    // user must be instructor for the section
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(
            @PathVariable("sectionNo") int sectionNo ) {
        // TODO
        //  hint: use enrollment repository findEnrollmentsBySectionNoOrderByStudentName method
        //  remove the following line when done
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);
        List<EnrollmentDTO> enrollmentDTOs = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            enrollmentDTOs.add(new EnrollmentDTO(
                    enrollment.getEnrollmentId(),
                    enrollment.getGrade(),
                    enrollment.getStudent().getStudentId(),
                    enrollment.getStudent().getName(),
                    enrollment.getStudent().getEmail(),
                    enrollment.getSection().getCourse().getCourseId(),
                    enrollment.getSection().getCourse().getTitle(),
                    enrollment.getSection().getSectionId(),
                    enrollment.getSection().getSectionNo(),
                    enrollment.getSection().getBuilding(),
                    enrollment.getSection().getRoom(),
                    enrollment.getSection().getTimes(),
                    enrollment.getSection().getCourse().getCredits(),
                    enrollment.getSection().getYear(),
                    enrollment.getSection().getSemester()
            ));
        }
        return enrollmentDTOs;
    }

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist) {

        // TODO
        // For each EnrollmentDTO in the list
        //  find the Enrollment entity using enrollmentId
        //  update the grade and save back to database
        for (EnrollmentDTO dto : dlist) {
            Enrollment enrollment = enrollmentRepository.findById(dto.enrollmentId).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
            enrollment.setGrade(dto.grade);
            enrollmentRepository.save(enrollment);
        }
    }
}
