package com.scholarscore.models.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.StudentAssignment;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 11/17/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentSectionDashboardData implements Serializable {
    protected Section section;
    protected List<StudentAssignment> studentAssignments;
    protected SectionGradeWithProgression gradeProgression;

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<StudentAssignment> getStudentAssignments() {
        return studentAssignments;
    }

    public void setStudentAssignments(List<StudentAssignment> studentAssignments) {
        this.studentAssignments = studentAssignments;
    }

    public SectionGradeWithProgression getGradeProgression() {
        return gradeProgression;
    }

    public void setGradeProgression(SectionGradeWithProgression gradeProgression) {
        this.gradeProgression = gradeProgression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, studentAssignments, gradeProgression);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final StudentSectionDashboardData other = (StudentSectionDashboardData) obj;
        return Objects.equals(this.section, other.section)
                && Objects.equals(this.studentAssignments, other.studentAssignments)
                && Objects.equals(this.gradeProgression, other.gradeProgression);
    }
}
