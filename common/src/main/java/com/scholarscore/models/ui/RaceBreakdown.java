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
                case "W":
                    totalWhite.add(student);
                    break;
                case "B":
                    totalBlack.add(student);
                    break;
                case "A":
                    totalAsian.add(student);
                    break;
                case "I":
                    totalIndian.add(student);
                    break;
                case "P":
                    totalPacific.add(student);
                    break;
                case "H":
                    totalHispanic.add(student);
                    break;
            }
        }

    }

    public void addFailingGrade(Student student) {
        String studentRace = student.getFederalRace();
        countFailing(student,studentRace, "W", whiteStudentsFailing);
        countFailing(student,studentRace, "B", blackStudentsFailing);
        countFailing(student,studentRace, "A", asianStudentsFailing);
        countFailing(student,studentRace, "I", indianStudentsFailing);
        countFailing(student,studentRace, "P", pacificStudentsFailing);
        countFailing(student,studentRace, "H", hispanicStudentsFailing);

    }

    public ArrayList<ArrayList<Object>> buildReturnObject() {
        addStudentDatapoints("White", totalWhite, whiteStudentsFailing);
        addStudentDatapoints("Black", totalBlack, blackStudentsFailing);
        addStudentDatapoints("Asian", totalAsian, asianStudentsFailing);
        addStudentDatapoints("Indian", totalIndian, indianStudentsFailing);
        addStudentDatapoints("Pacific", totalPacific, pacificStudentsFailing);
        addStudentDatapoints("Hispanic", totalHispanic, hispanicStudentsFailing);

        return super.buildReturnObject();
    }
}
