package com.scholarscore.models.state.ma;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Student;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * Created by markroper on 4/10/16.
 */
@Entity(name = HibernateConsts.MA_MCAS_TABLE)
public class McasResult {
    protected Long id;
    protected String sasid;
    protected String schoolName;
    protected Long schoolId;
    protected Long adminYear;
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

    @Transient
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Column(name = HibernateConsts.SCHOOL_FK)
    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.MA_MCAS_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Column(name = HibernateConsts.MA_MCAS_EXAM_GRADE_LEVEL)
    public Long getExamGradeLevel() {
        return examGradeLevel;
    }

    public void setExamGradeLevel(Long examGradeLevel) {
        this.examGradeLevel = examGradeLevel;
    }

    @Column(name = HibernateConsts.MA_MCAS_STUD_GRADE_LEVEL)
    public Long getStudentGradeLevel() {
        return studentGradeLevel;
    }

    public void setStudentGradeLevel(Long studentGradeLevel) {
        this.studentGradeLevel = studentGradeLevel;
    }

    @OneToOne(fetch= FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = HibernateConsts.MA_MCAS_ENGLISH_FK)
    @Fetch(FetchMode.JOIN)
    public McasTopicScore getEnglishScore() {
        return englishScore;
    }

    public void setEnglishScore(McasTopicScore englishScore) {
        this.englishScore = englishScore;
    }

    @Column(name = HibernateConsts.MA_MCAS_TOPIC_SCORE)
    public Double getEnglishTopicScore() {
        return englishTopicScore;
    }

    public void setEnglishTopicScore(Double englishTopicScore) {
        this.englishTopicScore = englishTopicScore;
    }

    @Column(name = HibernateConsts.MA_MCAS_COMPOSITION_SCORE)
    public Double getEnglishCompositionScore() {
        return englishCompositionScore;
    }

    public void setEnglishCompositionScore(Double englishCompositionScore) {
        this.englishCompositionScore = englishCompositionScore;
    }

    @Column(name = HibernateConsts.MA_MCAS_ADMIN_YEAR)
    public Long getAdminYear() {
        return adminYear;
    }

    public void setAdminYear(Long adminYear) {
        this.adminYear = adminYear;
    }

    @OneToOne(fetch= FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = HibernateConsts.MA_MCAS_MATH_FK)
    @Fetch(FetchMode.JOIN)
    public McasTopicScore getMathScore() {
        return mathScore;
    }

    public void setMathScore(McasTopicScore mathScore) {
        this.mathScore = mathScore;
    }

    @OneToOne(fetch= FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = HibernateConsts.MA_MCAS_SCIENCE_FK)
    @Fetch(FetchMode.JOIN)
    public McasTopicScore getScienceScore() {
        return scienceScore;
    }

    public void setScienceScore(McasTopicScore scienceScore) {
        this.scienceScore = scienceScore;
    }

    @Column(name = HibernateConsts.STUDENT_STATE_ID)
    public String getSasid() {
        return sasid;
    }

    public void setSasid(String sasid) {
        this.sasid = sasid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, examGradeLevel, studentGradeLevel, englishScore, adminYear, sasid,
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
                && Objects.equals(this.adminYear, other.adminYear)
                && Objects.equals(this.sasid, other.sasid)
                && Objects.equals(this.scienceScore, other.scienceScore);
    }
}
