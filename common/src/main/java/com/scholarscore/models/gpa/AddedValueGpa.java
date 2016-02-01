package com.scholarscore.models.gpa;

import com.scholarscore.models.grade.StudentSectionGrade;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.util.Collection;

/**
 * @author by markroper on 11/23/15.
 */
@Entity
@DiscriminatorValue(value = "ADDED_VALUE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AddedValueGpa extends Gpa {
    @Override
    @Transient
    public GpaTypes getType() {
        return GpaTypes.ADDED_VALUE;
    }

    @Override
    public Double calculateGpa(Collection<StudentSectionGrade> sections) {
        return null;
    }
}
