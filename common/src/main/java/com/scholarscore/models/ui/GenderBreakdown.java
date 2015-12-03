package com.scholarscore.models.ui;

import com.scholarscore.models.Gender;
import com.scholarscore.models.user.Student;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

/**
 * Created by cwallace on 12/3/15.
 */
public class GenderBreakdown {
    //Rename to utility?
    private HashSet<Student> totalMales = new HashSet<>();
    private HashSet<Student> totalFemales = new HashSet<>();
    private HashSet<Student> totalOther = new HashSet<>();
    Map<Student, Integer> maleStudentsFailing = new HashMap<>();
    Map<Student, Integer> femaleStudentsFailing = new HashMap<>();
    private Integer maxClassesFailed = 0;
    private ArrayList<ArrayList<Object>> chartingArray = new ArrayList<>();

    public GenderBreakdown() {

    }

    public void addToTotal(Student student) {
        if (student.getGender() == Gender.MALE) {
            totalMales.add(student);
        } else if (student.getGender() == Gender.FEMALE) {
            totalFemales.add(student);
        } else {
            totalOther.add(student);
        }
    }

    public void addFailingGrade(Student student) {
        countFailing(student, Gender.MALE, maleStudentsFailing);
        countFailing(student, Gender.FEMALE, femaleStudentsFailing);

    }

    public ArrayList<ArrayList<Object>> buildReturnObject() {
        addStudentDatapoints("Male", totalMales, maleStudentsFailing);
        addStudentDatapoints("Female", totalFemales, femaleStudentsFailing);

        ArrayList<Object> xAxis = new ArrayList<>();

        xAxis.add("Students failing");
        for (int i = 0; i <= maxClassesFailed; i++) {
            xAxis.add(i);
        }
        chartingArray.add(xAxis);
        return chartingArray;
    }

    private void countFailing(Student student, Gender gender, Map<Student,Integer> studentsFailing) {
        if (student.getGender() == gender) {
            Integer sectionsFailed = studentsFailing.get(student);
            if (null != sectionsFailed) {
                studentsFailing.put(student, sectionsFailed + 1);
                if (maxClassesFailed < sectionsFailed + 1) {
                    maxClassesFailed = sectionsFailed + 1;
                }
            } else {
                studentsFailing.put(student, 1);
                if (maxClassesFailed < 1) {
                    maxClassesFailed = 1;
                }
            }
        }
    }
    private void addStudentDatapoints(String axisDisplay, HashSet<Student> totalStudents, Map<Student, Integer> studentsFailing) {
        ArrayList<Object> studentDatapoints = new ArrayList<>();
        studentDatapoints.add(axisDisplay);
        studentDatapoints.add(totalStudents.size() - studentsFailing.size());
        for (int i = 0; i < maxClassesFailed; i++) {
            studentDatapoints.add(0);
        }

        Iterator<Map.Entry<Student, Integer>> it = studentsFailing.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Student, Integer> entry = it.next();
            Integer failingIndex = entry.getValue()+1;
            Integer presentNumberFailing = (Integer)studentDatapoints.get(failingIndex);
            studentDatapoints.set(failingIndex,presentNumberFailing+1);

        }
        chartingArray.add(studentDatapoints);
    }


}
