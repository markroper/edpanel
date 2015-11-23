package com.scholarscore.models.gpa;

import com.scholarscore.models.StudentSectionGrade;

import java.util.Collection;

/**
 * @author by markroper on 11/23/15.
 */
public class WeightedGpa extends Gpa {
    @Override
    public GpaTypes getType() {
        return GpaTypes.WEIGHTED;
    }

    @Override
    public Double calculateGpa(Collection<StudentSectionGrade> sections) {
        return null;
    }
}
