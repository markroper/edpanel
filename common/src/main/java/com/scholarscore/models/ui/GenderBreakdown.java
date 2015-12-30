package com.scholarscore.models.ui;

import com.scholarscore.models.Gender;
import com.scholarscore.models.user.Student;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

/**
 * Created by cwallace on 12/3/15.
 */
public class GenderBreakdown extends Breakdown{
    //Rename to utility?
    private HashSet<Student> totalMales = new HashSet<>();
    private HashSet<Student> totalFemales = new HashSet<>();
    private HashSet<Student> totalOther = new HashSet<>();
    Map<Student, Integer> maleStudentsFailing = new HashMap<>();
    Map<Student, Integer> femaleStudentsFailing = new HashMap<>();


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
        String studentGender = student.getGender().toString();
        countFailing(student,studentGender, Gender.MALE.toString(), maleStudentsFailing);
        countFailing(student,studentGender, Gender.FEMALE.toString(), femaleStudentsFailing);

    }

    public ArrayList<ArrayList<Object>> buildReturnObject() {
        addStudentDatapoints("Male", totalMales, maleStudentsFailing);
        addStudentDatapoints("Female", totalFemales, femaleStudentsFailing);

        return super.buildReturnObject();
    }



}
