package com.cst438.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {

    // In the second query, value and nativeQuery assignments were added
    // because Anthony was encountering errors when running on VS code.
    // These can be removed if errors are not encountered by others. 

    @Query("select a from Assignment a where a.section.sectionNo=:sectionNo order by a.dueDate")
    List<Assignment> findBySectionNoOrderByDueDate(int sectionNo);

    @Query(value = "select a from Assignment a join a.section.enrollments e " +
            "where a.section.term.year=:year and a.section.term.semester=:semester and" +
            " e.student.id=:studentId order by a.dueDate", nativeQuery = true)
    List<Assignment> findByStudentIdAndYearAndSemesterOrderByDueDate(int studentId, int year, String semester);

}
