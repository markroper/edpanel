package com.scholarscore.etl.powerschool.api.model.assignment;

import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;

import java.util.HashSet;

/**
 * Contains a static factory method for creating an EdPanel Assignment instance from a powerschool
 * assignment instance and a powerschool assignment category instance.
 *
 * Created by markroper on 10/22/15.
 */
public class PsAssignmentFactory {
    private static final HashSet<String> HW_STRINGS = new HashSet<String>(){{
        add("HW"); add("HOMEWORK"); add("HOME WORK");
    }};
    private static final HashSet<String> TEST_STRINGS = new HashSet<String>(){{
        add("TST"); add("TEST"); add("TESTS"); add("CT");
    }};
    private static final HashSet<String> QUIZ_STRINGS = new HashSet<String>(){{
        add("QUIZ"); add("QUIZZES"); add("QZ");
    }};
    private static final HashSet<String> CLASSWORK_STRINGS = new HashSet<String>(){{
        add("CLASSWORK"); add("CW"); add("CLASS WORK");
    }};
    private static final HashSet<String> SUMMATIVE_ASSESSMENT_STRINGS = new HashSet<String>(){{
        add("SUMMATIVE ASSESSMENTS"); add("SA");
    }};
    private static final HashSet<String> INTERIM_ASSESSMENT_STRINGS = new HashSet<String>(){{
        add("INTERIM ASSESSMENTS"); add("IA");
    }};
    private static final HashSet<String> PARTICIPATION_STRINGS = new HashSet<String>(){{
        add("CLASS PARTICIPATION"); add("CP"); add("PARTICIPATION"); add("DISC");
    }};
    private static final HashSet<String> WRITTEN_WORK_STRINGS = new HashSet<String>(){{
        add("WRITTEN WORK"); add("WW");
    }};
    private static final HashSet<String> PREP_WORK_STRINGS = new HashSet<String>(){{
        add("PREP WORK"); add("PREPWORK"); add("PW");
    }};
    private static final HashSet<String> ATTENDANCE_STRINGS = new HashSet<String>(){{
        add("ATTENDANCE"); add("AT");
    }};
    private static final HashSet<String> LAB_STRINGS = new HashSet<String>(){{
        add("LAB");
    }};
    private static final HashSet<String> MIDTERM_STRINGS = new HashSet<String>(){{
        add("MIDTERM"); add("MID TERM");
    }};
    private static final HashSet<String> FINAL_STRINGS = new HashSet<String>(){{
        add("FINAL"); add("FINAL EXAM");
    }};

    private static final HashSet<String> EXAM_STRINGS = new HashSet<String>(){{
        add("EXAM");
    }};
    private static final HashSet<String> PROJ_STRINGS = new HashSet<String>(){{
        add("PROJECT"); add("PROJ");
    }};

    public static Assignment fabricate(PsAssignment powerAss, PsAssignmentType powerType) {
        String abb = powerType.getAbbreviation().toUpperCase();
        String name = powerType.getName().toUpperCase();
        Assignment a;
        if(ATTENDANCE_STRINGS.contains(abb) || ATTENDANCE_STRINGS.contains(name)) {
            a = new AttendanceAssignment();
        } else {
            a = new GradedAssignment();
        }
        a.setType(resolveAssignmentType(powerType.getAbbreviation(), powerType.getName()));
        populateInstance(a, powerAss);
        return a;
    }

    public static AssignmentType resolveAssignmentType(String abb, String name) {
        abb = abb.toUpperCase();
        name = name.toUpperCase();
        if(HW_STRINGS.contains(abb) || HW_STRINGS.contains(name)) {
            return AssignmentType.HOMEWORK;
        } else if(TEST_STRINGS.contains(abb) || TEST_STRINGS.contains(name)) {
            return AssignmentType.TEST;
        } else if(QUIZ_STRINGS.contains(abb) || QUIZ_STRINGS.contains(name)) {
            return AssignmentType.QUIZ;
        } else if(CLASSWORK_STRINGS.contains(abb) || CLASSWORK_STRINGS.contains(name)) {
            return AssignmentType.CLASSWORK;
        } else if(SUMMATIVE_ASSESSMENT_STRINGS.contains(abb) || SUMMATIVE_ASSESSMENT_STRINGS.contains(name)) {
            return AssignmentType.SUMMATIVE_ASSESSMENT;
        } else if(INTERIM_ASSESSMENT_STRINGS.contains(abb) || INTERIM_ASSESSMENT_STRINGS.contains(name)) {
            return AssignmentType.INTERIM_ASSESSMENT;
        } else if(PARTICIPATION_STRINGS.contains(abb) || PARTICIPATION_STRINGS.contains(name)) {
            return AssignmentType.PARTICIPATION;
        } else if(WRITTEN_WORK_STRINGS.contains(abb) || WRITTEN_WORK_STRINGS.contains(name)) {
            return AssignmentType.WRITTEN_WORK;
        } else if(PREP_WORK_STRINGS.contains(abb) || PREP_WORK_STRINGS.contains(name)) {
            return AssignmentType.USER_DEFINED;
        } else if(ATTENDANCE_STRINGS.contains(abb) || ATTENDANCE_STRINGS.contains(name)) {
            return AssignmentType.ATTENDANCE;
        } else if(LAB_STRINGS.contains(abb) || LAB_STRINGS.contains(name)) {
            return AssignmentType.LAB;
        } else if(MIDTERM_STRINGS.contains(abb) || MIDTERM_STRINGS.contains(name)) {
            return AssignmentType.MIDTERM;
        } else if(FINAL_STRINGS.contains(abb) || FINAL_STRINGS  .contains(name)) {
            return AssignmentType.FINAL;
        } else if(EXAM_STRINGS.contains(abb) || EXAM_STRINGS.contains(name)) {
            return AssignmentType.EXAM;
        } else if(PROJ_STRINGS.contains(abb) || PROJ_STRINGS.contains(name)){
            return AssignmentType.PROJECT;
        } else {
            return AssignmentType.USER_DEFINED;
        }
    }

    private static void populateInstance(Assignment a, PsAssignment p) {
        a.setAvailablePoints(p.getPointspossible());
        a.setDueDate(p.getDatedue());
        a.setName(p.getName());
        if(a instanceof GradedAssignment) {
            ((GradedAssignment) a).setAssignedDate(p.getPublishspecificdate());
        }
    }
}