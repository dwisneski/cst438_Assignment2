package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

   // student gets transcript showing list of all enrollments
   // studentId will be temporary until Login security is implemented
   //example URL  /transcript?studentId=19803
   @GetMapping("/transcripts")
   public List<EnrollmentDTO> getTranscript(@RequestParam("studentId") int studentId) {
       List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
       List<EnrollmentDTO> enrollmentDTOs = new ArrayList<>();
       for (Enrollment enrollment : enrollments) {
           enrollmentDTOs.add(new EnrollmentDTO(
                   enrollment.getEnrollmentId(),
                   enrollment.getGrade(),
                   enrollment.getUser().getId(),
                   enrollment.getUser().getName(),
                   enrollment.getUser().getEmail(),
                   enrollment.getSection().getCourse().getCourseId(),
                   enrollment.getSection().getCourse().getTitle(),
                   enrollment.getSection().getSecId(),
                   enrollment.getSection().getSectionNo(),
                   enrollment.getSection().getBuilding(),
                   enrollment.getSection().getRoom(),
                   enrollment.getSection().getTimes(),
                   enrollment.getSection().getCourse().getCredits(),
                   enrollment.getSection().getTerm().getYear(),
                   enrollment.getSection().getTerm().getSemester()
           ));
       }
       return enrollmentDTOs;
   }

    // student gets a list of their enrollments for the given year, semester
    // user must be student
    // studentId will be temporary until Login security is implemented
   @GetMapping("/enrollments")
   public List<EnrollmentDTO> getSchedule(
           @RequestParam("year") int year,
           @RequestParam("semester") String semester,
           @RequestParam("studentId") int studentId) {
       List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
       List<EnrollmentDTO> enrollmentDTOs = new ArrayList<>();
       for (Enrollment enrollment : enrollments) {
           enrollmentDTOs.add(new EnrollmentDTO(
                   enrollment.getEnrollmentId(),
                   enrollment.getGrade(),
                   enrollment.getUser().getId(),
                   enrollment.getUser().getName(),
                   enrollment.getUser().getEmail(),
                   enrollment.getSection().getCourse().getCourseId(),
                   enrollment.getSection().getCourse().getTitle(),
                   enrollment.getSection().getSecId(),
                   enrollment.getSection().getSectionNo(),
                   enrollment.getSection().getBuilding(),
                   enrollment.getSection().getRoom(),
                   enrollment.getSection().getTimes(),
                   enrollment.getSection().getCourse().getCredits(),
                   enrollment.getSection().getTerm().getYear(),
                   enrollment.getSection().getTerm().getSemester()
           ));
       }
       return enrollmentDTOs;
   }


    // student adds enrollment into a section
    // user must be student
    // return EnrollmentDTO with enrollmentId generated by database
    @PostMapping("/enrollments/sections/{sectionNo}")
    public EnrollmentDTO addCourse(
		    @PathVariable int sectionNo,
            @RequestParam("studentId") int studentId ) {

        // TODO

        // check that the Section entity with primary key sectionNo exists
        // check that today is between addDate and addDeadline for the section
        // check that student is not already enrolled into this section
        // create a new enrollment entity and save.  The enrollment grade will
        // be NULL until instructor enters final grades for the course.

        // remove the following line when done.
        return null;

    }

    // student drops a course
    // user must be student
   @DeleteMapping("/enrollments/{enrollmentId}")
   public void dropCourse(@PathVariable("enrollmentId") int enrollmentId) {

       // TODO
       // check that today is not after the dropDeadline for section
   }
}