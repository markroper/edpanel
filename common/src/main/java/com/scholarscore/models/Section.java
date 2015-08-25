package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A Section is a temporal instance of a Course.  Where a course defines that which is to be taught, a Section has
 * a reference to a course, and in addition, a start and end date, a set of enrolled students, a room, a grade book,
 * and so on.
 * 
 * @author markroper
 *
 */
@Entity
@Table(name = "section")
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section extends ApiModel implements Serializable, IApiModel<Section> {
    protected Date startDate;
    protected Date endDate;
    protected String room;
    protected GradeFormula gradeFormula;
    protected Long termFK;
    protected transient Course course;
    protected transient List<Student> enrolledStudents;
    protected transient List<Assignment> assignments;
    //TODO: List<Teacher> teachers;
    //TODO: Set<SectionAssignment> assignments;
    //TODO: Schedule
    //TODO: Gradebook - student -> SectionGrade { overallGrade, List<StudentAssignment>, homeworkGradeAvergae, quizGradeAvg }
    
    public Section() {
        super();
    }
    
    public Section(Date startDate, Date endDate, String room, GradeFormula gradeFormula) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.room = room;
        this.gradeFormula = gradeFormula;
    }
    
    public Section(Section sect) {
        super(sect);
        course = sect.course;
        startDate = sect.startDate;
        endDate = sect.endDate;
        room = sect.room;
        enrolledStudents = sect.enrolledStudents;
        assignments = sect.assignments;
        gradeFormula = sect.gradeFormula;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "section_id")
    public Long getId() {
        return super.getId();
    }

    @Column(name = "term_fk")
    public Long getTermFK() {
        return termFK;
    }

    public void setTermFK(Long termFK) {
        this.termFK = termFK;
    }

    @Override
    @Column(name = "section_name")
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name="course_fk")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Column(name = "section_start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "section_end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "room")
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Transient
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public Student findEnrolledStudentById(Long id) {
        Student student = null;
        if(null != id && null != enrolledStudents && !enrolledStudents.isEmpty()) {
            for(Student s: enrolledStudents) {
                if(s.getId().equals(id)) {
                    student = s;
                    break;
                }
            }
        }
        return student;
    }

    @Transient
    public List<Assignment> getAssignments() {
        return assignments;
    }
    
    public Assignment findAssignmentById(Long id) {
        Assignment assignment = null;
        if(null != id && null != assignments && !assignments.isEmpty()) {
            for(Assignment s: assignments) {
                if(s.getId().equals(id)) {
                    assignment = s;
                    break;
                }
            }
        }
        return assignment;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    @Transient
    public GradeFormula getGradeFormula() {
        return gradeFormula;
    }

    public void setGradeFormula(GradeFormula gradeFormula) {
        this.gradeFormula = gradeFormula;
    }

    @Override
    public void mergePropertiesIfNull(Section mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == mergeFrom) { return; }
        if(null == course) {
            course = mergeFrom.course;
        }
        if(null == startDate) {
            startDate = mergeFrom.startDate;
        }
        if(null == endDate) {
            endDate = mergeFrom.endDate;
        }
        if(null == room) {
            room = mergeFrom.room;
        }
        if(null == enrolledStudents) {
            enrolledStudents = mergeFrom.enrolledStudents;
        }
        if(null == assignments) {
            assignments = mergeFrom.assignments;
        }
        if(null == gradeFormula) {
            gradeFormula = mergeFrom.gradeFormula;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final Section other = (Section) obj;
        return Objects.equals(this.course, other.course) && 
                Objects.equals(this.startDate, other.startDate) && 
                Objects.equals(this.endDate, other.endDate) &&
                Objects.equals(this.room, other.room) &&
                Objects.equals(this.enrolledStudents, other.enrolledStudents) &&
                Objects.equals(this.assignments, other.assignments) &&
                Objects.equals(this.gradeFormula, other.gradeFormula);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(course, startDate, endDate, 
                room, enrolledStudents, assignments, gradeFormula);
    }
    
}
