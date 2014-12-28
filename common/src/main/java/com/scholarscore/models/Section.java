package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A Section is a temporal instance of a Course.  Where a course defines that which is to be taught, a Section has
 * a reference to a course, and in addition, a start and end date, a set of enrolled students, a room, a grade book,
 * and so on.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section extends ApiModel implements Serializable, IApiModel<Section> {
    Long courseId;
    Long termId;
    Long yearId;
    
    Date startDate;
    Date endDate;
    String room;
    Set<Long> enrolledStudents;
    Map<Long, SectionAssignment> sectionAssignments;
    GradeFormula gradeFormula;
    //TODO: List<Teacher> teachers;
    //TODO: Set<SectionAssignment> assignments;
    //TODO: Schedule
    //TODO: Gradebook - student -> SectionGrade { overallGrade, List<StudentAssignment>, homeworkGradeAvergae, quizGradeAvg }
    
    public Section() {
        super();
    }
    
    public Section(Section sect) {
        super(sect);
        courseId = sect.courseId;
        termId = sect.termId;
        yearId = sect.yearId;
        startDate = sect.startDate;
        endDate = sect.endDate;
        room = sect.room;
        enrolledStudents = sect.enrolledStudents;
        sectionAssignments = sect.sectionAssignments;
        gradeFormula = sect.gradeFormula;
    }
    
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Long getYearId() {
        return yearId;
    }

    public void setYearId(Long yearId) {
        this.yearId = yearId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Set<Long> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Set<Long> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public Map<Long, SectionAssignment> getSectionAssignments() {
        return sectionAssignments;
    }

    public void setSectionAssignments(Map<Long, SectionAssignment> sectionAssignments) {
        this.sectionAssignments = sectionAssignments;
    }

    public GradeFormula getGradeFormula() {
        return gradeFormula;
    }

    public void setGradeFormula(GradeFormula gradeFormula) {
        this.gradeFormula = gradeFormula;
    }

    @Override
    public void mergePropertiesIfNull(Section mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == courseId) {
            courseId = mergeFrom.courseId;
        }
        if(null == termId) {
            termId = mergeFrom.termId;
        }
        if(null == yearId) {
            yearId = mergeFrom.yearId;
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
        if(null == sectionAssignments) {
            sectionAssignments = mergeFrom.sectionAssignments;
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
        return Objects.equals(this.courseId, other.courseId) && 
                Objects.equals(this.termId, other.termId) &&
                Objects.equals(this.yearId, other.yearId) &&
                Objects.equals(this.startDate, other.startDate) && 
                Objects.equals(this.endDate, other.endDate) &&
                Objects.equals(this.room, other.room) &&
                Objects.equals(this.enrolledStudents, other.enrolledStudents) &&
                Objects.equals(this.sectionAssignments, other.sectionAssignments) &&
                Objects.equals(this.gradeFormula, other.gradeFormula);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(courseId, termId, yearId, startDate, endDate, 
                room, enrolledStudents, sectionAssignments, gradeFormula);
    }
    
}
