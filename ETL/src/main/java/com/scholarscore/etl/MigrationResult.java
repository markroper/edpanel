package com.scholarscore.etl;

import com.scholarscore.models.School;
import com.scholarscore.models.Student;

import java.util.List;

/**
 * The entities created via the migration process
 *
 * Created by mattg on 7/3/15.
 */
public class MigrationResult {
    public List<School> schools;
    public List<Student> students;

    @Override
    public String toString() {
        return "MigrationResult{" +
                "schools=" + schools +
                ", students=" + students +
                '}';
    }
}
