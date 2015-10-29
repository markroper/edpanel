package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.models.ApiModel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The ISync interface should be implemented for every entity being migrated & synchronized from
 * PowerSchool to EdPanel.  The idea is that calling `syncCreateUpdateDelete()` will synchronize all instances
 * of T between PowerSchool and EdPanel where discrepencies will be resolved using the PowerSchool state.
 *
 * See the implementing classes below for reference.
 *
 * @see SchoolSync
 * @see TermSync
 * @see CourseSync
 * @see com.scholarscore.etl.powerschool.sync.user.StudentSync
 * @see com.scholarscore.etl.powerschool.sync.user.StaffSync
 * @see com.scholarscore.etl.powerschool.sync.section.SectionSyncRunnable
 * @see com.scholarscore.etl.powerschool.sync.assignment.SectionAssignmentSync
 * @see com.scholarscore.etl.powerschool.sync.section.StudentSectionGradeSync
 * @see com.scholarscore.etl.powerschool.sync.assignment.StudentAssignmentSyncRunnable
 *
 * Created by markroper on 10/26/15.
 */
public interface ISync<T extends ApiModel> {

    /**
     * <p>
     * Pulls all the instances of T (scoped to the unit of migration for the implementation) from both PowerSchool
     * and EdPanel, transforms the PowerSchool representation into an EdPanel model, and then resolves and
     * performs the following operations for each instance:
     * <p/>
     * <p>
     *  1) If the entity exists in PowerSchool and not in EdPanel, *CREATE* it in EdPanel
     *  2) If the entity exists in both PowerSchool and EdPanel, but has different values, *UPDATE* it in EdPanel
     *  3) If the entity exists in EdPanel but not in PowerSchool, *DELETE* it in EdPanel
     *  4) If the entity exists in both EdPanel and PowerSchool and its values are the same, perform no action.
     * </p>
     * <p>
     * Implementations should be idempotent, which is to say that re-running this method after it has been previously
     * run (successfully or unsuccessfully as in a crash) should yield the same end state within EdPanel as if it
     * had been run for the first time.
     * </p>
     *
     * @return A thread safe map of PowerSchool ID (source system id) to EdPanel model that represents the
     *          end state of what is in EdPanel after all the CREATE/UPDATE/DELETE operations have been
     *          performed to synchronize EdPanel's state with PowerSchool's state.
     */
    ConcurrentHashMap<Long, T> syncCreateUpdateDelete();

}
