package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.api.util.SchoolDataFactory;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.Teacher;
import com.scholarscore.models.Term;

/**
 * Generates synthetic data for school, school years, terms, courses, sections, students, teachers, 
 * assignments and student assignments.
 * 
 * @author markroper
 *
 */
@Test(groups = { "datagen" })
public class UISyntheticDatagenerator extends IntegrationBase {
    School school;
    
    public void seedDatabase() {
        authenticate();
        
        //Create school
        school = schoolValidatingExecutor.create(
                SchoolDataFactory.generateSchool(), "Create base school");
        
        //Create teachers
        List<Teacher> createdTeachers = new ArrayList<Teacher>();
        for(Teacher t : SchoolDataFactory.generateTeachers(school.getId())) {
            createdTeachers.add(teacherValidatingExecutor.create(t, t.getName()));
        }
        
        //Create students
        List<Student> generatedStudents = new ArrayList<Student>();
        for(Student s: SchoolDataFactory.generateStudents(school.getId())) {
            generatedStudents.add(studentValidatingExecutor.create(s, s.getName()));
        }
        
        //School years
        List<SchoolYear> generatedSchoolYears = new ArrayList<SchoolYear>();
        for(SchoolYear year: SchoolDataFactory.generateSchoolYears()) {
            generatedSchoolYears.add(
                    schoolYearValidatingExecutor.create(school.getId(), year, year.getName()));
        }
        
        //Create courses
        List<Course> generatedCourses = new ArrayList<Course>();
        for(Course c : SchoolDataFactory.generateCourses(school.getId())) {
            generatedCourses.add(
                    courseValidatingExecutor.create(school.getId(), c, c.getName()));
        }
        
        //Create terms
        Map<Long, List<Term>> terms = SchoolDataFactory.generateTerms(generatedSchoolYears);
        for(Map.Entry<Long, List<Term>> termEntry : terms.entrySet()) {
            List<Term> createdTerms = new ArrayList<Term>();
            for(Term currentTerm : termEntry.getValue()) {
                Term createdTerm = termValidatingExecutor.create(
                        school.getId(), 
                        termEntry.getKey(), 
                        currentTerm, 
                        currentTerm.getName());
                createdTerms.add(createdTerm);
            }
            
            //Create the sections for courses in the school and the terms in the current schoolYear
            Map<Long, List<Section>> sections = 
                    SchoolDataFactory.generateSections(createdTerms, generatedCourses, generatedStudents);
            for(Map.Entry<Long, List<Section>> sectionEntry : sections.entrySet()) {
                List<Section> createdSections = new ArrayList<Section>();
                for(Section section : sectionEntry.getValue()) {
                    createdSections.add(sectionValidatingExecutor.create(
                            school.getId(), 
                            termEntry.getKey(), 
                            sectionEntry.getKey(), 
                            section, 
                            section.getName()));
                }
                
                //Create the assignments for each of the sections in the current term
                Map<Long, List<Assignment>> assignments = 
                        SchoolDataFactory.generateAssignments(createdSections);
                for(Map.Entry<Long, List<Assignment>> assignmentEntry: assignments.entrySet()) {
                    List<Assignment> createdAssignments = new ArrayList<Assignment>();
                    for(Assignment ass : assignmentEntry.getValue()) {
                        createdAssignments.add(
                                sectionAssignmentValidatingExecutor.create(
                                        school.getId(),
                                        termEntry.getKey(),
                                        sectionEntry.getKey(),
                                        assignmentEntry.getKey(),
                                        ass,
                                        ass.getName()
                                        ));
                    }
                    
                    //Create the student assignments for each student for list of assignments in term sections
                    Map<Long, List<StudentAssignment>> studentAssignments = 
                            SchoolDataFactory.generateStudentAssignments(createdAssignments, generatedStudents);
                    for(Map.Entry<Long, List<StudentAssignment>> studentAssignmentEntry : studentAssignments.entrySet()) {
                        List<StudentAssignment> createdStudentAssignments = new ArrayList<StudentAssignment>();
                        for(StudentAssignment sa : studentAssignmentEntry.getValue()) {
                            createdStudentAssignments.add(
                                    studentAssignmentValidatingExecutor.create(
                                            school.getId(), 
                                            termEntry.getKey(), 
                                            sectionEntry.getKey(), 
                                            assignmentEntry.getKey(),
                                            sa.getAssignment().getId(), 
                                            sa, 
                                            sa.getName()));
                        }
                    }  
                }
            }
        }
    }
    
    @Override
    protected void removeTestData() {
        //NO-OP!
    }
}
