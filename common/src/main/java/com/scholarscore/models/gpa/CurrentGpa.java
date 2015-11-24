package com.scholarscore.models.gpa;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.Student;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by markroper on 11/24/15.
 */
@Entity(name = HibernateConsts.CURRENT_GPA_TABLE)
@Table(name = HibernateConsts.CURRENT_GPA_TABLE)
public class CurrentGpa {
    protected Gpa gpa;
    protected Student student;
    protected Long id;

    @Id
    @Column(name = HibernateConsts.CURRENT_GPA_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(optional = true, fetch= FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = HibernateConsts.GPA_FK)
    public Gpa getGpa() {
        return gpa;
    }

    public void setGpa(Gpa gpa) {
        this.gpa = gpa;
    }

    @OneToOne(optional = true, fetch= FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = HibernateConsts.STUDENT_FK)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gpa, id, student);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CurrentGpa other = (CurrentGpa) obj;
        return Objects.equals(this.gpa, other.gpa)
                && Objects.equals(this.student, other.student)
                && Objects.equals(this.id, other.id);
    }
}
