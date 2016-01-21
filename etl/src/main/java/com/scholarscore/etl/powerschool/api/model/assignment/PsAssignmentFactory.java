package com.scholarscore.etl.powerschool.api.model.assignment;

import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.AttendanceAssignment;
import com.scholarscore.models.assignment.GradedAssignment;

import java.util.HashSet;

/**
 * Contains a static factory method for creating an EdPanel Assignment instance from a powerschool
 * assignment instance and a powerschool assignment category instance.  There are multiple test-like
 * assignment types and subtypes defined by users.  We bucket subtypes into the following EdPanel AssignmentTypes:
 *
 *                    TEST             QUIZ     MIDTERM     FINAL   HOMEWORK         CLASSWORK
 *          /       /     \      \                                  /    \               |
 *  mastery  summative  interim  exam                             HW  prepwork       classwork
 *
 *
 * Created by markroper on 10/22/15.
 */
public class PsAssignmentFactory {
    private static final HashSet<String> HW_STRINGS = new HashSet<String>(){{
        add("HW"); add("HOMEWORK"); add("HOME WORK");
    }};
    private static final HashSet<String> TEST_STRINGS = new HashSet<String>(){{
        add("TST"); add("TEST"); add("TESTS"); add("MASTERY ASSESSMENTS"); add("MQ"); add("ASSESSMENT");
    }};
    private static final HashSet<String> QUIZ_STRINGS = new HashSet<String>(){{
        add("QUIZ"); add("QUIZZES"); add("QZ");
    }};
    private static final HashSet<String> CLASSWORK_STRINGS = new HashSet<String>(){{
        add("CLASSWORK"); add("CW"); add("CLASS WORK"); add("CLASS");
    }};
    private static final HashSet<String> SUMMATIVE_ASSESSMENT_STRINGS = new HashSet<String>(){{
        add("SUMMATIVE ASSESSMENTS");
    }};
    private static final HashSet<String> INTERIM_ASSESSMENT_STRINGS = new HashSet<String>(){{
        add("INTERIM ASSESSMENTS"); add("INTERIM");
    }};
    private static final HashSet<String> PARTICIPATION_STRINGS = new HashSet<String>(){{
        add("CLASS PARTICIPATION"); add("PARTICIPATION"); add("DISC");
    }};
    private static final HashSet<String> WRITTEN_WORK_STRINGS = new HashSet<String>(){{
        add("WRITTEN WORK"); add("WRITING"); add("ESSAY"); add("PAPER");
    }};
    private static final HashSet<String> PREP_WORK_STRINGS = new HashSet<String>(){{
        add("PREP WORK"); add("PREPWORK"); add("PW");
    }};
    private static final HashSet<String> ATTENDANCE_STRINGS = new HashSet<String>(){{
        add("ATTENDANCE");
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
        add("EXAM"); add("QUARTERLY EXAM");
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
        a.setType(resolveAssignmentType(powerType.getAbbreviation(), powerType.getName(), powerAss.getName()));
        populateInstance(a, powerAss);
        return a;
    }

    private static AssignmentType checkAssigmentType(String toCheck) {
        if(HW_STRINGS.contains(toCheck)) {
            return AssignmentType.HOMEWORK;
        } else if(TEST_STRINGS.contains(toCheck)) {
            return AssignmentType.TEST;
        } else if(QUIZ_STRINGS.contains(toCheck)) {
            return AssignmentType.QUIZ;
        } else if(CLASSWORK_STRINGS.contains(toCheck)) {
            return AssignmentType.CLASSWORK;
        } else if(SUMMATIVE_ASSESSMENT_STRINGS.contains(toCheck)) {
            return AssignmentType.TEST;
        } else if(INTERIM_ASSESSMENT_STRINGS.contains(toCheck)) {
            return AssignmentType.TEST;
        } else if(PARTICIPATION_STRINGS.contains(toCheck)) {
            return AssignmentType.PARTICIPATION;
        } else if(WRITTEN_WORK_STRINGS.contains(toCheck)) {
            return AssignmentType.WRITTEN_WORK;
        } else if(ATTENDANCE_STRINGS.contains(toCheck)) {
            return AssignmentType.ATTENDANCE;
        } else if(PREP_WORK_STRINGS.contains(toCheck)) {
            //Treat prepwork as homework within EdPanel
            return AssignmentType.HOMEWORK;
        } else if(LAB_STRINGS.contains(toCheck)) {
            return AssignmentType.LAB;
        } else if(MIDTERM_STRINGS.contains(toCheck)) {
            return AssignmentType.MIDTERM;
        } else if(FINAL_STRINGS.contains(toCheck)) {
            return AssignmentType.FINAL;
        } else if(EXAM_STRINGS.contains(toCheck)) {
            return AssignmentType.TEST;
        } else if(PROJ_STRINGS.contains(toCheck)){
            return AssignmentType.PROJECT;
        } else {
            return null;
        }
    }

    public static AssignmentType resolveAssignmentType(String abbreviation, String nameOriginal, String assignmentName) {
        abbreviation = abbreviation.toUpperCase();
        nameOriginal = nameOriginal.toUpperCase();

        AssignmentType type = PsAssignmentFactory.checkAssigmentType(abbreviation);
        if (null != type) {
            return type;
        }

        type = PsAssignmentFactory.checkAssigmentType(nameOriginal);
        if (null != type) {
            return type;
        }

        type = resolveAssignmentTypeWildcard(abbreviation, nameOriginal, assignmentName);
        if(null != type) {
            return type;
        }

        String[] nameArray = assignmentName.split(" ");
        for (String name: nameArray) {
            name = name.replaceAll("[^a-zA-Z0-9]+","");
            name = name.toUpperCase();
            type = PsAssignmentFactory.checkAssigmentType(name);
            if (null != type) {
                return type;
            }
        }


            return AssignmentType.USER_DEFINED;

    }

    private static AssignmentType resolveAssignmentTypeWildcard(String abbreviation, String nameOriginal, String assignmentName) {
        if(null == abbreviation) {
            abbreviation = "";
        }
        if(null == nameOriginal) {
            nameOriginal = "";
        }
        if(null == assignmentName) {
            assignmentName = "";
        }
        String cAbbreviation = abbreviation.toUpperCase();
        String cNameOrig = nameOriginal.toUpperCase();
        String cAssName = assignmentName.toUpperCase();
        for(String s: HW_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.HOMEWORK;
            }
        }
        for(String s: WRITTEN_WORK_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.WRITTEN_WORK;
            }
        }
        for(String s: LAB_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.LAB;
            }
        }
        for(String s: CLASSWORK_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.CLASSWORK;
            }
        }
        for(String s: PROJ_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.PROJECT;
            }
        }
        for(String s: SUMMATIVE_ASSESSMENT_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.TEST;
            }
        }
        for(String s: INTERIM_ASSESSMENT_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.TEST;
            }
        }
        for(String s: PARTICIPATION_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.PARTICIPATION;
            }
        }
        for(String s: ATTENDANCE_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.ATTENDANCE;
            }
        }
        for(String s: MIDTERM_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.MIDTERM;
            }
        }
        for(String s: FINAL_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.FINAL;
            }
        }
        for(String s: EXAM_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.TEST; //SHOULD 'QUARTERLY EXAM' BUCKET TO EXAM OR TEST?
            }
        }
        for(String s: PREP_WORK_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.HOMEWORK;
            }
        }
        for(String s: TEST_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.TEST;
            }
        }
        for(String s: QUIZ_STRINGS) {
            if(cAbbreviation.contains(s) || cNameOrig.contains(s) || cAssName.contains(s)) {
                return AssignmentType.QUIZ;
            }
        }
        return null;
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
