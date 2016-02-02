package com.scholarscore.models.grade;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A SectionGrade represents the grade for a single student in a single class on a specfic date.
 * StudentSectionGrade points to the most recent SectionGrade for a student in section, but the
 * database stores SectionGrades over time so that EdPanel can support historical values.
 *
 * Created by markroper on 1/26/16.
 */
@Entity(name = HibernateConsts.SECTION_GRADE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionGrade extends Score {
    private LocalDate date;
    private Long sectionFk;
    private Long id;
    private Long studentFk;

    public SectionGrade() {

    }

    public SectionGrade(Score s) {
        this.date = LocalDate.now();
        this.manuallyOverridden = s.manuallyOverridden;
        this.comment = s.comment;
        this.letterGrade = s.letterGrade;
        this.score = s.score;
        this.termId = s.termId;
    }
    @Column(name = HibernateConsts.SECTION_GRADE_DATE)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Column(name = HibernateConsts.SECTION_FK)
    public Long getSectionFk() {
        return sectionFk;
    }

    public void setSectionFk(Long sectionFk) {
        this.sectionFk = sectionFk;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.SECTION_GRADE_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.STUDENT_FK)
    public Long getStudentFk() {
        return studentFk;
    }

    public void setStudentFk(Long studentFk) {
        this.studentFk = studentFk;
    }

    @Column(name = HibernateConsts.SECTION_GRADE_COMMENT, columnDefinition = "blob")
    public String getComment() {
        return comment;
    }

    @Column(name = HibernateConsts.SECTION_GRADE_MANUALLY_OVERRIDDEN)
    public Boolean getManuallyOverridden() {
        return manuallyOverridden;
    }

    @Column(name = HibernateConsts.SECTION_GRADE_LETTER_GRADE)
    public String getLetterGrade() {
        return letterGrade;
    }

    @Column(name = HibernateConsts.SECTION_GRADE_GRADE)
    public Double getScore() {
        return score;
    }

    @Column(name = HibernateConsts.TERM_FK)
    public Long getTermId() {
        return termId;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(date, sectionFk, id, studentFk);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final SectionGrade other = (SectionGrade) obj;
        return Objects.equals(this.date, other.date)
                && Objects.equals(this.sectionFk, other.sectionFk)
                && Objects.equals(this.studentFk, other.studentFk)
                && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "SectionGrade{(super:" + super.toString() + ")" +
                "date=" + date +
                ", sectionFk=" + sectionFk +
                ", id=" + id +
                ", studentFk=" + studentFk +
                '}';
    }
}
