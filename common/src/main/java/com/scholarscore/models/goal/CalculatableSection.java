package com.scholarscore.models.goal;

import com.scholarscore.models.Section;
import com.scholarscore.models.user.Student;

/**
 * Interface for defining that a goal/component
 * Created by cwallace on 10/15/2015.
 */
public interface CalculatableSection {

    public Section getSection();

    public void setSection(Section section);

    public void setStudent(Student student);

    public Student getStudent();
}
