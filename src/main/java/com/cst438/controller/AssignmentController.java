package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(
            @PathVariable("secNo") int secNo) {Section section = sectionRepository.findBySectionNum(secNo);
        List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);
        List<AssignmentDTO> assignmentDTOS = new ArrayList<>();
        for (Assignment assignment : assignments) {
            assignmentDTOS.add(new AssignmentDTO(
                    assignment.getAssignmentId(),
                    assignment.getTitle(),
                    assignment.getDueDate(),
                    assignment.getSection().getCourse().getCourseId(),
                    assignment.getSection().getSecId(),
                    assignment.getSection().getSectionNo()
            ));
        }
        return assignmentDTOS;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(
            @RequestBody AssignmentDTO dto) {

        Assignment a = new Assignment();

        Section section = sectionRepository.findBySectionNum(dto.secNo());
        if (section == null) {
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "secId is invalid");
        }
        a.setSection(section);
        a.setTitle(dto.title());
        a.setDueDate(dto.dueDate());
        assignmentRepository.save(a);
        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDate(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()
        );
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {
        Assignment assignment = assignmentRepository.findById(dto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        assignment.setTitle(dto.title());
        assignment.setDueDate(dto.dueDate());
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return new AssignmentDTO(
                updatedAssignment.getAssignmentId(),
                updatedAssignment.getTitle(),
                updatedAssignment.getDueDate(),
                updatedAssignment.getSection().getCourse().getCourseId(),
                updatedAssignment.getSection().getSecId(),
                updatedAssignment.getSection().getSectionNo()
        );
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        assignmentRepository.delete(assignment);
    }

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {

        // TODO remove the following line when done

        // get the list of enrollments for the section related to this assignment.
        // hint: use te enrollment repository method findEnrollmentsBySectionOrderByStudentName.
        // for each enrollment, get the grade related to the assignment and enrollment
        //   hint: use the gradeRepository findByEnrollmentIdAndAssignmentId method.
        //   if the grade does not exist, create a grade entity and set the score to NULL
        //   and then save the new entity

        return null;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {

        // TODO

        // for each grade in the GradeDTO list, retrieve the grade entity
        // update the score and save the entity

    }



    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    // return a list of assignments and (if they exist) the assignment grade
    //  for all sections that the student is enrolled for the given year and semester
    //  hint: use the assignment repository method findByStudentIdAndYearAndSemesterOrderByDueDate
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {
        List<Assignment> assignments = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId, year, semester);
        List<AssignmentStudentDTO> assignmentStudentDTOS = new ArrayList<>();

        for (Assignment assignment : assignments) {
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(assignment.getAssignmentId(), studentId);
            assignmentStudentDTOS.add(new AssignmentStudentDTO(
                    assignment.getAssignmentId(),
                    assignment.getTitle(),
                    assignment.getDueDate(),
                    assignment.getSection().getCourse().getCourseId(),
                    assignment.getSection().getSecId(),
                    grade != null ? grade.getScore() : null
            ));
        }

        return assignmentStudentDTOS;
    }

}
