package com.scholarscore.etl;

/**
 * Created by mattg on 7/3/15.
 */
public interface IEtlEngine {

    /**
     * Synchronize a district from the source system(s) to EdPanel.  Implementations should be idempotent
     * and suitable for both initial migration and a repeated sync.  Implementations should proceed with
     * the following order of operations:
     *
     *  1) Synchronize all schools in the district (CRUD)
     *  2) Synchronize all students and staff for all schools (CRUD)
     *  3) Synchronize all courses for the district (CRUD)
     *  4) Synchronize all school years for all schools in the district (CRUD)
     *  5) Synchronize all terms for all schools (CRUD)
     *  6) Synchronize all sections for all schools (Recommend adding multi-threading here)
     *      i.   Synchronize the section definition
     *      ii.  Synchronize the Student section grades
     *      iii. Synchronize the assignments
     *      iv.  Synchronize the student scores on the assignments
     * @return A descriptive migration result indicating what was done and what failed during the sync
     */
    SyncResult syncDistrict();
}
