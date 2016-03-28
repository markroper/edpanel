package com.scholarscore.models.gpa;

import com.scholarscore.models.grade.StudentSectionGrade;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.util.Collection;

/**
 * Created by markroper on 3/28/16.
 */
@Entity
@DiscriminatorValue(value = "SIMPLE_ADDED_VALUE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SimpleAddedValueGpa extends Gpa {
    @Override
    @Transient
    public GpaTypes getType() {
        return GpaTypes.SIMPLE_ADDED_VALUE;
    }

    @Override
    public Double calculateGpa(Collection<StudentSectionGrade> sections) {
        return null;
    }
}
