package com.scholarscore.models;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

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
@Entity(name = HibernateConsts.SECTION_TABLE)
@Table(name = HibernateConsts.SECTION_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section extends ApiModel implements Serializable, IApiModel<Section> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    protected Date startDate;
    protected Date endDate;
    protected String room;
    //For jackson & for java 
    protected GradeFormula gradeFormula;
    //For hibernate
    protected String gradeFormulaString;

    protected Term term;
    protected transient Course course;
    protected transient List<Student> enrolledStudents;
    protected transient List<Assignment> assignments;
    protected List<StudentSectionGrade> studentSectionGrades;
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
    @Column(name = HibernateConsts.SECTION_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.SECTION_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.COURSE_FK)
    @Fetch(FetchMode.JOIN)
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.TERM_FK)
    @Fetch(FetchMode.JOIN)
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Column(name = HibernateConsts.SECTION_START_DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.SECTION_END_DATE)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = HibernateConsts.SECTION_ROOM)
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    
    @JsonIgnore
    @Column(name = HibernateConsts.SECTION_GRADE_FORMULA)
    public String getGradeFormulaString() {
        return this.gradeFormulaString;
    }

    @JsonIgnore
    public void setGradeFormulaString(String string) {
        if(null == string) {
            this.gradeFormula = null;
            this.gradeFormulaString = null;
        } else {
            try {
                this.gradeFormulaString = string;
                this.gradeFormula = MAPPER.readValue( string, GradeFormula.class);
            } catch (IOException e) {
                this.gradeFormula =  null;
                this.gradeFormulaString = null;
            }
        }
    }

    @Transient
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
    
    @OneToMany(mappedBy = "section", fetch=FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    @Fetch(FetchMode.JOIN)
    @JsonIgnore
    public List<StudentSectionGrade> getStudentSectionGrades() {
        return studentSectionGrades;
    }

    public void setStudentSectionGrades(List<StudentSectionGrade> grades) {
        this.studentSectionGrades = grades;
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
        if(null == gradeFormula) {
            this.gradeFormula = null;
            this.gradeFormulaString = null;
        } else {
            try {
                this.gradeFormula = gradeFormula;
                this.gradeFormulaString = MAPPER.writeValueAsString(gradeFormula);
            } catch (JsonProcessingException e) {
                this.gradeFormulaString = null;
                this.gradeFormula = null;
            }
        }
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
