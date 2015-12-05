package com.scholarscore.models.ui;

import com.scholarscore.models.Gender;
import com.scholarscore.models.user.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by cwallace on 12/3/15.
 */
public class RaceBreakdown extends Breakdown {
    private static final String WHITE = "W";
    private static final String BLACK = "B";
    private static final String HISPANIC = "I";
    private static final String INDIAN = "I";
    private static final String PACIFIC = "P";
    private static final String ASIAN = "A";

    private HashSet<Student> totalWhite = new HashSet<>();
    private HashSet<Student> totalBlack = new HashSet<>();
    private HashSet<Student> totalAsian = new HashSet<>();
    private HashSet<Student> totalIndian = new HashSet<>();
    private HashSet<Student> totalHispanic = new HashSet<>();
    private HashSet<Student> totalPacific = new HashSet<>();

    Map<Student, Integer> whiteStudentsFailing = new HashMap<>();
    Map<Student, Integer> blackStudentsFailing = new HashMap<>();
    Map<Student, Integer> asianStudentsFailing = new HashMap<>();
    Map<Student, Integer> indianStudentsFailing = new HashMap<>();
    Map<Student, Integer> hispanicStudentsFailing = new HashMap<>();
    Map<Student, Integer> pacificStudentsFailing = new HashMap<>();

    public void addToTotal(Student student) {
        if (null == student.getFederalRace()) {
            totalWhite.add(student);
        } else {
            switch (student.getFederalRace()) {
                case WHITE:
                    totalWhite.add(student);
                    break;
                case BLACK:
                    totalBlack.add(student);
                    break;
                case ASIAN:
                    totalAsian.add(student);
                    break;
                case INDIAN:
                    if (student.getFederalEthnicity().equals("YES")) {
                        totalHispanic.add(student);
                    } else {
                        totalIndian.add(student);
                    }

                    break;
                case PACIFIC:
                    totalPacific.add(student);
                    break;
            }
        }

    }

    public void addFailingGrade(Student student) {
        String studentRace = student.getFederalRace();
        countFailing(student,studentRace, WHITE, whiteStudentsFailing, null);
        countFailing(student,studentRace, BLACK, blackStudentsFailing, null);
        countFailing(student,studentRace, ASIAN, asianStudentsFailing, null);
        countFailing(student,studentRace, INDIAN, indianStudentsFailing, false);
        countFailing(student,studentRace, PACIFIC, pacificStudentsFailing, null);
        countFailing(student,studentRace, HISPANIC, hispanicStudentsFailing, true);

    }

    public ArrayList<ArrayList<Object>> buildReturnObject() {
        addStudentDatapoints("Caucasian", totalWhite, whiteStudentsFailing);
        addStudentDatapoints("African American", totalBlack, blackStudentsFailing);
        addStudentDatapoints("Asian", totalAsian, asianStudentsFailing);
        addStudentDatapoints("American Indian", totalIndian, indianStudentsFailing);
        addStudentDatapoints("Pacific Islander", totalPacific, pacificStudentsFailing);
        addStudentDatapoints("Hispanic or Latino", totalHispanic, hispanicStudentsFailing);

        return super.buildReturnObject();
    }
}
