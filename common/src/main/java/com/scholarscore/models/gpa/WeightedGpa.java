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
@DiscriminatorValue(value = "WEIGHTED")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class WeightedGpa extends Gpa {
    @Override
    @Transient
    public GpaTypes getType() {
        return GpaTypes.WEIGHTED;
    }

    @Override
    public Double calculateGpa(Collection<StudentSectionGrade> sections) {
        return null;
    }
}
