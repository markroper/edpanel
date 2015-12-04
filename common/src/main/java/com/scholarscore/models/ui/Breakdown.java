package com.scholarscore.models.ui;

import com.scholarscore.models.user.Student;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by cwallace on 12/3/15.
 */
public abstract class Breakdown {
    protected Integer maxClassesFailed = 0;
    protected ArrayList<ArrayList<Object>> chartingArray = new ArrayList<>();

    protected void countFailing(Student student, String studentValue, String comparitor, Map<Student,Integer> studentsFailing, Boolean isHispanic) {
        if (comparitor.equals(studentValue)) {
            if (null == isHispanic) {
                incrementMaxClasses(student, studentsFailing);
            } else {
                if (isHispanic) {
                    if (("YES").equals(student.getFederalEthnicity())) {
                        incrementMaxClasses(student, studentsFailing);
                    }
                } else {
                    if (("NO").equals(student.getFederalEthnicity())) {
                        incrementMaxClasses(student, studentsFailing);
                    }
                }
            }

        }
    }

    private void incrementMaxClasses(Student student, Map<Student, Integer> studentsFailing) {
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

    protected void addStudentDatapoints(String axisDisplay, HashSet<Student> totalStudents, Map<Student, Integer> studentsFailing) {
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
        if (totalStudents.size() != 0) {
            chartingArray.add(studentDatapoints);
        }

    }

    public ArrayList<ArrayList<Object>> buildReturnObject() {

        ArrayList<Object> xAxis = new ArrayList<>();

        xAxis.add("Students failing");
        for (int i = 0; i <= maxClassesFailed; i++) {
            xAxis.add(i);
        }
        chartingArray.add(xAxis);
        return chartingArray;
    }
}
