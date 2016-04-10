package com.scholarscore.models.state.ma;

import com.scholarscore.models.user.Student;

import java.util.Objects;

/**
 * Created by markroper on 4/10/16.
 */
public class McasResult {
    protected Long id;
    protected String schoolName;
    protected Long schoolId;
    protected Student student;
    protected Long examGradeLevel;
    protected Long studentGradeLevel;
    //English
    protected McasTopicScore englishScore;
    protected Double englishTopicScore;
    protected Double englishCompositionScore;
    //Math
    protected McasTopicScore mathScore;
    //Science & Tech
    protected McasTopicScore scienceScore;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Long getExamGradeLevel() {
        return examGradeLevel;
    }

    public void setExamGradeLevel(Long examGradeLevel) {
        this.examGradeLevel = examGradeLevel;
    }

    public Long getStudentGradeLevel() {
        return studentGradeLevel;
    }

    public void setStudentGradeLevel(Long studentGradeLevel) {
        this.studentGradeLevel = studentGradeLevel;
    }

    public McasTopicScore getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(McasTopicScore englishScore) {
        this.englishScore = englishScore;
    }

    public Double getEnglishTopicScore() {
        return englishTopicScore;
    }

    public void setEnglishTopicScore(Double englishTopicScore) {
        this.englishTopicScore = englishTopicScore;
    }

    public Double getEnglishCompositionScore() {
        return englishCompositionScore;
    }

    public void setEnglishCompositionScore(Double englishCompositionScore) {
        this.englishCompositionScore = englishCompositionScore;
    }

    public McasTopicScore getMathScore() {
        return mathScore;
    }

    public void setMathScore(McasTopicScore mathScore) {
        this.mathScore = mathScore;
    }

    public McasTopicScore getScienceScore() {
        return scienceScore;
    }

    public void setScienceScore(McasTopicScore scienceScore) {
        this.scienceScore = scienceScore;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, examGradeLevel, studentGradeLevel, englishScore,
                englishTopicScore, englishCompositionScore, mathScore, scienceScore, schoolName, schoolId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final McasResult other = (McasResult) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.examGradeLevel, other.examGradeLevel)
                && Objects.equals(this.studentGradeLevel, other.studentGradeLevel)
                && Objects.equals(this.englishScore, other.englishScore)
                && Objects.equals(this.englishTopicScore, other.englishTopicScore)
                && Objects.equals(this.englishCompositionScore, other.englishCompositionScore)
                && Objects.equals(this.mathScore, other.mathScore)
                && Objects.equals(this.schoolName, other.schoolName)
                && Objects.equals(this.schoolId, other.schoolId)
                && Objects.equals(this.scienceScore, other.scienceScore);
    }
}
