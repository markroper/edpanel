package com.scholarscore.api.controller.uiControllers;

import com.scholarscore.models.user.Student;

import java.util.Objects;

/**
 * Created by markroper on 4/15/16.
 */
public class AssignmentResult {
    private Double score;
    private Student student;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, student);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentResult other = (AssignmentResult) obj;
        return Objects.equals(this.score, other.score)
                && Objects.equals(this.student, other.student);
    }
}
