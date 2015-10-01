package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.api.util.SchoolDataFactory;

import com.scholarscore.models.*;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.SchoolDay;

import org.testng.annotations.Test;

import java.util.*;

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
        
        for(SchoolYear y : generatedSchoolYears) {
            Date currentDate = y.getStartDate();
            Calendar c = Calendar.getInstance();
            
            //Create the school days
            while(currentDate.compareTo(y.getEndDate()) < 0) {
                c.setTime(currentDate);
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                if(dayOfWeek != 1 && dayOfWeek != 7) {
                    //create a school day!
                    SchoolDay day = new SchoolDay();
                    day.setSchool(school);
                    day.setDate(currentDate);
                    day = schoolDayValidatingExecutor.create(school.getId(), day, "Creating a school date for synthetic data generator");
                    for(Student s : generatedStudents) {
                        int val = new Random().nextInt(100);
                        AttendanceStatus status = AttendanceStatus.PRESENT;
                        //one in ten times, choose a random attendance status
                        if(val != 0 && val % 10 == 0) {
                            status = AttendanceStatus.values()[new Random().nextInt(AttendanceStatus.values().length - 1)];
                        }
                        Attendance a = new Attendance();
                        a.setSchoolDay(day);
                        a.setStudent(s);
                        a.setStatus(status);
                        attendanceValidatingExecutor.create(school.getId(), s.getId(), a, "creating attendance");
                    }
                }
                //Increment the date
                c.add(Calendar.DATE, 1);
                currentDate = c.getTime();
            }
        }
        
        //Create courses
        List<Course> generatedCourses = new ArrayList<Course>();
        for(Course c : SchoolDataFactory.generateCourses(school.getId())) {
            generatedCourses.add(
                    courseValidatingExecutor.create(school.getId(), c, c.getName()));
        }
        
        //Create terms
        Map<Long, List<Term>> terms = SchoolDataFactory.generateTerms(generatedSchoolYears);
        Map<Long, List<Long>> studentToSectionId = new HashMap<Long, List<Long>>();
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
                    SchoolDataFactory.generateSections(createdTerms, generatedCourses, generatedStudents, createdTeachers);

            for(Map.Entry<Long, List<Section>> sectionEntry : sections.entrySet()) {
                List<Section> createdSections = new ArrayList<Section>();

                for(Section section : sectionEntry.getValue()) {
                    Section createdSection = sectionValidatingExecutor.create(
                            school.getId(),
                            termEntry.getKey(),
                            sectionEntry.getKey(),
                            section,
                            section.getName());
                    createdSections.add(createdSection);
                    for(Student s : section.getEnrolledStudents()) {
                        StudentSectionGrade ssg = new StudentSectionGrade();
                        ssg.setComplete(true);
                        ssg.setGrade(100D - new Random().nextInt(35));
                        ssg.setSection(createdSection);
                        ssg.setStudent(s);
                        StudentSectionGrade savedGrade = studentSectionGradeValidatingExecutor.update(
                                school.getId(),
                                termEntry.getKey(),
                                sectionEntry.getKey(),
                                createdSection.getId(),
                                s.getId(),
                                ssg,
                                "Updating student section grade");
                        //Needed for giving goals
                        if (null == studentToSectionId.get(s.getId())) {
                            List<Long> sectionIds = new ArrayList<Long>();
                            sectionIds.add(section.getId());
                            studentToSectionId.put(s.getId(),sectionIds);
                        } else {
                            studentToSectionId.get(s.getId()).add(createdSection.getId());
                        }
                        if(null == savedGrade) {
                            System.out.println("failed to update SSG");
                        }
                    }
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
//                    Map<Long, List<StudentAssignment>> studentAssignments =
//                            SchoolDataFactory.generateStudentAssignments(createdAssignments, generatedStudents);
//                    for(Map.Entry<Long, List<StudentAssignment>> studentAssignmentEntry : studentAssignments.entrySet()) {
//                        List<StudentAssignment> createdStudentAssignments = new ArrayList<StudentAssignment>();
//                        for(StudentAssignment sa : studentAssignmentEntry.getValue()) {
//                            createdStudentAssignments.add(
//                                    studentAssignmentValidatingExecutor.create(
//                                            school.getId(),
//                                            termEntry.getKey(),
//                                            sectionEntry.getKey(),
//                                            assignmentEntry.getKey(),
//                                            sa.getAssignment().getId(),
//                                            sa,
//                                            sa.getName()));
//                        }
//                    }
                }
            }
        }
        
        Date beginDate = new Date();
        Date endDate = new Date();
        for(Map.Entry<Long, List<Term>> yearEntry: terms.entrySet()) {
            for(Term t : yearEntry.getValue()) {
                if(t.getStartDate().getTime() < beginDate.getTime()) {
                    beginDate = t.getStartDate();
                }
                if(t.getEndDate().getTime() > endDate.getTime()) {
                    endDate = t.getEndDate();
                }
            }
        }
        Map<Long, ArrayList<Behavior>> behaviors = 
                SchoolDataFactory.generateBehaviorEvents(generatedStudents, createdTeachers, beginDate, endDate);
        for(Map.Entry<Long, ArrayList<Behavior>> studentBehaviorEntry : behaviors.entrySet()) {
            for(Behavior b : studentBehaviorEntry.getValue()) {
                behaviorValidatingExecutor.create(
                        studentBehaviorEntry.getKey(), 
                        b, 
                        "Creating randomly generated student");
            }
        }

        Map<Long, ArrayList<Goal>> goals = SchoolDataFactory.generateGoalEvents(generatedStudents,
                teachersCreated.get(0),
                beginDate,
                endDate,
                studentToSectionId);
        for(Map.Entry<Long, ArrayList<Goal>> studentGoalEntry : goals.entrySet()) {
            for (Goal goal : studentGoalEntry.getValue()) {
                goalValidatingExecutor.create(
                        studentGoalEntry.getKey(),
                        goal,
                        "Creating randomly generated goal"
                );
            }
        }
    }
    
    @Override
    protected void removeTestData() {
        //NO-OP!
    }
}
