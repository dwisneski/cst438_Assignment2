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
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

   // student gets transcript showing list of all enrollments
   // studentId will be temporary until Login security is implemented
   //example URL  /transcript?studentId=19803
   @GetMapping("/transcripts")
   public List<EnrollmentDTO> getTranscript(@RequestParam("studentId") int studentId) {
       User student = userRepository.findStudentById(studentId);
       if (student == null) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "studentId is invalid");
       }
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
       User student = userRepository.findStudentById(studentId);
       if (student == null) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "studentId is invalid");
       }
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
        Section section = sectionRepository.findById(sectionNo).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));;

        User student = userRepository.findStudentById(studentId);
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "studentId is invalid");
        }

        var today = new java.util.Date();
        var addDate = new java.util.Date(section.getTerm().getAddDate().getTime());
        var addDeadline = new java.util.Date(section.getTerm().getAddDeadline().getTime());
        var outsideDateRange = today.before(addDate) || today.after(addDeadline);
        if (outsideDateRange) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot add a course before the add date or after the add deadline");
        }
        // check that student is not already enrolled into this section
        var enrollment = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionNo, studentId);
        if (enrollment != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student is already enrolled in this section");
        }
        enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setSection(section);
        enrollmentRepository.save(enrollment);
        return new EnrollmentDTO(
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
        );

    }

    // student drops a course
    // user must be student
   @DeleteMapping("/enrollments/{enrollmentId}")
   public void dropCourse(@PathVariable("enrollmentId") int enrollmentId) {
       Enrollment e = enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId);
       if (e == null) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "enrollmentId is invalid");
       }
       // check that today is not after the dropDeadline for section
       var today = new java.util.Date();
       var deadline = new java.util.Date(e.getSection().getTerm().getDropDeadline().getTime());
       var afterDropDeadline = today.after(deadline);
       if (afterDropDeadline) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the deadline to drop this enrollment has passed");
       }
       enrollmentRepository.delete(e);
   }
}