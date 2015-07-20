package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.dimension.CourseDimension;
import com.scholarscore.models.query.dimension.GradeLevelDimension;
import com.scholarscore.models.query.dimension.IDimension;
import com.scholarscore.models.query.dimension.SchoolDimension;
import com.scholarscore.models.query.dimension.SchoolYearDimension;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.dimension.SubjectAreaDimension;
import com.scholarscore.models.query.dimension.TeacherDimension;
import com.scholarscore.models.query.dimension.TermDimension;
import com.scholarscore.models.query.measure.AssignmentGradeMeasure;
import com.scholarscore.models.query.measure.CourseGradeMeasure;
import com.scholarscore.models.query.measure.GpaMeasure;
import com.scholarscore.models.query.measure.HomeworkCompletionMeasure;
import com.scholarscore.models.query.measure.IMeasure;

/**
 * This class encapsulates all Dimensions and Measures available for use in a valid Query.  It serializes to a form
 * that is consumable by a javascript front end and includes the dependency graph information needed by the frontend
 * to generate and validate query objects.
 * 
 * In order to support building or arbitrary queries, client code will request an instance of this object, 
 * inflate a relationship graph between the entities, and use this object to create the UI for building arbitrary 
 * queries to the system.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
public class QueryComponents implements Serializable {

    Set<IDimension> avalailableDimensions = ImmutableSet.of(new CourseDimension(), new GradeLevelDimension(), new SchoolDimension(),
            new SchoolYearDimension(), new SectionDimension(), new StudentDimension(), new SubjectAreaDimension(), new TeacherDimension(),
            new TermDimension());
    Set<IMeasure> availableMeasures = ImmutableSet.of(
            new AssignmentGradeMeasure(), new CourseGradeMeasure(), new GpaMeasure(), new HomeworkCompletionMeasure());
    public Set<IDimension> getAvalailableDimensions() {
        return avalailableDimensions;
    }
    public void setAvalailableDimensions(Set<IDimension> avalailableDimensions) {
        this.avalailableDimensions = avalailableDimensions;
    }
    public Set<IMeasure> getAvailableMeasures() {
        return availableMeasures;
    }
    public void setAvailableMeasures(Set<IMeasure> availableMeasures) {
        this.availableMeasures = availableMeasures;
    }
    
}
