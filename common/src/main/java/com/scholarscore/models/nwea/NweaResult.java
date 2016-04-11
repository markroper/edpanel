package com.scholarscore.models.nwea;

import com.scholarscore.models.user.Student;

import java.util.Objects;

/**
 * Created by cwallace on 4/11/16.
 */
public class NweaResult {

    private long id;
    private long schoolId;
    private long yearTaken;
    private NweaTerm termTaken;
    private Student student;
    private long examGradeLevel;
    private NweaSubject subject;
    long score;
    //TODO Add information about subcategories of results

    public NweaResult() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    public long getYearTaken() {
        return yearTaken;
    }

    public void setYearTaken(long yearTaken) {
        this.yearTaken = yearTaken;
    }

    public NweaTerm getTermTaken() {
        return termTaken;
    }

    public void setTermTaken(NweaTerm termTaken) {
        this.termTaken = termTaken;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public long getExamGradeLevel() {
        return examGradeLevel;
    }

    public void setExamGradeLevel(long examGradeLevel) {
        this.examGradeLevel = examGradeLevel;
    }

    public NweaSubject getSubject() {
        return subject;
    }

    public void setSubject(NweaSubject subject) {
        this.subject = subject;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schoolId, yearTaken, termTaken, student, examGradeLevel, subject, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final NweaResult other = (NweaResult) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.schoolId, other.schoolId)
                && Objects.equals(this.yearTaken, other.yearTaken)
                && Objects.equals(this.termTaken, other.termTaken)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.examGradeLevel, other.examGradeLevel)
                && Objects.equals(this.subject, other.subject)
                && Objects.equals(this.score, other.score);
    }
}
