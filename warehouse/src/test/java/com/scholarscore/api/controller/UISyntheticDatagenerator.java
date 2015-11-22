package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.api.util.SchoolDataFactory;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.UiAttributes;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        
        //create UI attributes for school
        UiAttributes attrs = UiAttributes.resolveDefaults(school);
        attrs = uiAttributesValidatingExecutor.create(school.getId(), attrs, "attrs to create");
        
        //Create teachers
        List<Teacher> createdTeachers = new ArrayList<Teacher>();
        for(Teacher t : SchoolDataFactory.generateTeachers(school.getId())) {
            createdTeachers.add(teacherValidatingExecutor.create(t, t.getName()));
        }

        List<Administrator> createdAdministrators = new ArrayList<>();
        for(Administrator a: SchoolDataFactory.generateAdmins(school.getId())) {
            createdAdministrators.add(userValidatingExecutor.createAdmin(a, a.getName()));
        }
        
        //Create students
        List<Student> generatedStudents = new ArrayList<Student>();
        for(Student s: SchoolDataFactory.generateStudents(school.getId())) {
            generatedStudents.add(studentValidatingExecutor.create(s, s.getName()));
        }
        
        //School years
        List<SchoolYear> generatedSchoolYears = new ArrayList<SchoolYear>();
        for(SchoolYear year: SchoolDataFactory.generateSchoolYears()) {
            year.setSchool(school);
            generatedSchoolYears.add(
                    schoolYearValidatingExecutor.create(school.getId(), year, year.getName()));
        }

        List<SchoolDay> daysToCreate = new ArrayList<>();
        List<Attendance> attendanceToCreate = new ArrayList<>();
        for(SchoolYear y : generatedSchoolYears) {
            LocalDate currentDate = y.getStartDate();
            //Create the school days
            while(currentDate.compareTo(y.getEndDate()) < 0) {
                int dayOfWeek = currentDate.getDayOfWeek().getValue();
                if(dayOfWeek != 6 && dayOfWeek != 7) {
                    //create a school day!
                    SchoolDay day = new SchoolDay();
                    day.setSchool(school);
                    day.setDate(currentDate);
                    daysToCreate.add(day);
                }
                //Increment the date
                currentDate = currentDate.plusDays(1l);
            }
        }
        Map<Long, List<Attendance>> studentIdToAttendance = new HashMap<>();
        List<Long> createdDayIds = schoolDayValidatingExecutor.createAll(
                school.getId(), daysToCreate, "creating days");
        int i = 0;
        for(SchoolDay day : daysToCreate) {
            day.setId(createdDayIds.get(i));
            i++;
            for(Student s : generatedStudents) {
                
                AttendanceStatus status = AttendanceStatus.PRESENT;
                //one in ten times (choose an int between 0 and 9, so 10% chance of 0), choose a random attendance status
                if(new Random().nextInt(10) == 0) {
                    status = AttendanceStatus.values()[new Random().nextInt(AttendanceStatus.values().length - 1)];
                }
                Attendance a = new Attendance();
                a.setSchoolDay(day);
                a.setType(AttendanceTypes.DAILY);
                a.setStudent(s);
                a.setStatus(status);
                if(!studentIdToAttendance.containsKey(s.getId())) {
                    studentIdToAttendance.put(s.getId(), new ArrayList<>());
                }
                studentIdToAttendance.get(s.getId()).add(a);
            }
        }
        Iterator<Map.Entry<Long, List<Attendance>>> it = studentIdToAttendance.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Long, List<Attendance>> entry = it.next();
            attendanceValidatingExecutor.createAll(
                    school.getId(),
                    entry.getKey(),
                    entry.getValue(),
                    "creating attendances for a student");
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
        Map<Long, List<Long>> studentToAssignmentId = new HashMap<Long, List<Long>>();
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
                    Map<Long, List<StudentAssignment>> studentAssignments =
                            SchoolDataFactory.generateStudentAssignments(createdAssignments, generatedStudents);
                    for(Map.Entry<Long, List<StudentAssignment>> studentAssignmentEntry : studentAssignments.entrySet()) {
                        List<StudentAssignment> createdStudentAssignments = new ArrayList<StudentAssignment>();

                        List<Long> studAssIds = studentAssignmentValidatingExecutor.createAll(
                                school.getId(),
                                termEntry.getKey(),
                                sectionEntry.getKey(),
                                assignmentEntry.getKey(),
                                studentAssignmentEntry.getKey(),
                                studentAssignmentEntry.getValue(),
                                "Bulk student assignment create");

                        int studAssIdIndex = 0;
                        for(StudentAssignment sa : studentAssignmentEntry.getValue()) {
                            sa.setId(studAssIds.get(studAssIdIndex));
                            createdStudentAssignments.add(sa);
                            //Needed for giving goals
                            if (null == studentToAssignmentId.get(sa.getStudent().getId())) {
                                List<Long> assignmentIds = new ArrayList<Long>();
                                assignmentIds.add(sa.getId());
                                studentToAssignmentId.put(sa.getStudent().getId(),assignmentIds);
                            } else {
                                studentToAssignmentId.get(sa.getStudent().getId()).add(sa.getId());
                            }
                        }

                    }
                }
            }
        }

        LocalDate beginDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        for(Map.Entry<Long, List<Term>> yearEntry: terms.entrySet()) {
            for(Term t : yearEntry.getValue()) {
                if(t.getStartDate().compareTo(beginDate) < 0) {
                    beginDate = t.getStartDate();
                }
                if(t.getEndDate().compareTo(endDate) > 0) {
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
                studentToSectionId,
                studentToAssignmentId);
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
