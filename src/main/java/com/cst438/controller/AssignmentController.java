package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(
            @PathVariable("secNo") int secNo) {

        // TODO remove the following line when done

        // hint: use the assignment repository method
        //  findBySectionNoOrderByDueDate to return
        //  a list of assignments

        return null;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(
            @RequestBody AssignmentDTO dto) {

        // TODO remove the following line when done

        return null;
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {

        // TODO remove the following line when done

        return null;
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {

        // TODO
    }

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {
        var a = assignmentRepository.findById(assignmentId).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AssignmentId not valid"));
        var enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(a.getSection().getSectionNo());
        List<GradeDTO> grades = new ArrayList<GradeDTO>();
        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollment.getEnrollmentId(), a.getAssignmentId());
            if (grade == null) {
                grade = new Grade();
                grade.setAssignment(a);
                grade.setEnrollment(enrollment);
            }
            gradeRepository.save(grade);
            grades.add(new GradeDTO(
                    grade.getGradeId(),
                    grade.getEnrollment().getUser().getName(),
                    grade.getEnrollment().getUser().getEmail(),
                    grade.getAssignment().getTitle(),
                    grade.getAssignment().getSection().getCourse().getCourseId(),
                    grade.getAssignment().getSection().getSecId(),
                    grade.getScore()));
        }
        return grades;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {
        for (GradeDTO dto : dlist) {
            Grade grade = gradeRepository.findById(dto.gradeId()).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));
            grade.setScore(dto.score());
            gradeRepository.save(grade);
        }
    }



    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

        // TODO remove the following line when done

        // return a list of assignments and (if they exist) the assignment grade
        //  for all sections that the student is enrolled for the given year and semester
		//  hint: use the assignment repository method findByStudentIdAndYearAndSemesterOrderByDueDate

        return null;
    }
}
