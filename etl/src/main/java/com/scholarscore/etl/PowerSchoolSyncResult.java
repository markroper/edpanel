package com.scholarscore.etl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/29/15.
 */
public class PowerSchoolSyncResult extends BaseSyncResult implements SyncResult {
    protected EntitySyncResult schools = new EntitySyncResult();
    protected EntitySyncResult schoolYears = new EntitySyncResult();
    protected EntitySyncResult terms = new EntitySyncResult();
    protected EntitySyncResult courses = new EntitySyncResult();
    protected EntitySyncResult students = new EntitySyncResult();
    protected EntitySyncResult staff = new EntitySyncResult();
    protected EntitySyncResult sections = new EntitySyncResult();
    protected EntitySyncResult schoolDays = new EntitySyncResult();
    protected EntitySyncResult attendance = new EntitySyncResult();
    protected EntitySyncResult cycles = new EntitySyncResult();
    protected EntitySyncResult periods = new EntitySyncResult();
    protected ConcurrentHashMap<Long, EntitySyncResult> sectionAssignments =
            new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, ConcurrentHashMap<Long, EntitySyncResult>> studentAssignments =
            new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, EntitySyncResult> studentSectionGrades =
            new ConcurrentHashMap<>();
    /*
        CREATE FAILED HELPER METHODS
    */
    public void schoolDayCreateFailed(long ssid) {
        schoolDays.failedCreate(ssid);
    }
    public void attendanceCreateFailed(long ssid) {
        attendance.failedCreate(ssid);
    }
    public void schoolCreateFailed(long ssid) {
        schools.failedCreate(ssid);
    }
    public void yearCreateFailed(long ssid) {
        schoolYears.failedCreate(ssid);
    }
    public void termCreateFailed(long ssid) {
        terms.failedCreate(ssid);
    }
    public void courseCreateFailed(long ssid) {
        courses.failedCreate(ssid);
    }
    public void studentCreateFailed(long ssid) {
        students.failedCreate(ssid);
    }
    public void staffCreateFailed(long ssid) {
        staff.failedCreate(ssid);
    }
    public void sectionCreateFailed(long ssid) {
        sections.failedCreate(ssid);
    }
    public void cycleCreateFailed(long ssid) { cycles.failedCreate(ssid);}  // these never get created, updated, deleted or pulled from edpanel
    public void periodCreateFailed(long ssid) { periods.failedCreate(ssid);} // these never get created, updated, deleted or pulled from edpanel
    public void sectionAssignmentCreateFailed(long sectionssid, long ssid) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
           result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.failedCreate(ssid);
    }
    public void studentAssignmentCreateFailed(long sectionssid, long assignmentssid, long ssid) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).failedCreate(ssid);
    }
    public void studentSectionGradeCreateFailed(long sectionssid, long ssid) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.failedCreate(ssid);
    }
    /*
        CREATE HELPER METHODS
    */
    public void schoolDayCreated(long ssid, long edPanelId) {
        schoolDays.created(ssid, edPanelId);
    }
    public void attendanceCreated(long ssid, long edPanelId) {
        attendance.created(ssid, edPanelId);
    }
    public void schoolCreated(long ssid, long edPanelId) {
        schools.created(ssid, edPanelId);
    }
    public void yearCreated(long ssid, long edPanelId) {
        schoolYears.created(ssid, edPanelId);
    }
    public void termCreated(long ssid, long edPanelId) {
        terms.created(ssid, edPanelId);
    }
    public void courseCreated(long ssid, long edPanelId) {
        courses.created(ssid, edPanelId);
    }
    public void studentCreated(long ssid, long edPanelId) {
        students.created(ssid, edPanelId);
    }
    public void staffCreated(long ssid, long edPanelId) {
        staff.created(ssid, edPanelId);
    }
    public void sectionCreated(long ssid, long edPanelId) {
        sections.created(ssid, edPanelId);
    }
    public void cycleCreated(long ssid, long edPanelId) { cycles.created(ssid, edPanelId);}
    public void periodCreated(long ssid, long edPanelId) { periods.created(ssid, edPanelId);}
    public void sectionAssignmentCreated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.created(ssid, edPanelId);
    }
    public void studentAssignmentCreated(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).created(ssid, edPanelId);
    }
    public void studentSectionGradeCreated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.created(ssid, edPanelId);
    }
    /*
        UPDATE FAILED HELPER METHODS
    */
    public void schoolDayUpdateFailed(long ssid, long edPanelId) {
        schoolDays.failedUpdate(ssid, edPanelId);
    }
    public void attendanceUpdateFailed(long ssid, long edPanelId) {
        attendance.failedUpdate(ssid, edPanelId);
    }
    public void schoolUpdateFailed(long ssid, long edPanelId) {
        schools.failedUpdate(ssid, edPanelId);
    }
    public void yearUpdateFailed(long ssid, long edPanelId) {
        schoolYears.failedUpdate(ssid, edPanelId);
    }
    public void termUpdateFailed(long ssid, long edPanelId) {
        terms.failedUpdate(ssid, edPanelId);
    }
    public void courseUpdateFailed(long ssid, long edPanelId) {
        courses.failedUpdate(ssid, edPanelId);
    }
    public void studentUpdateFailed(long ssid, long edPanelId) {
        students.failedUpdate(ssid, edPanelId);
    }
    public void staffUpdateFailed(long ssid, long edPanelId) {
        staff.failedUpdate(ssid, edPanelId);
    }
    public void sectionUpdateFailed(long ssid, long edPanelId) {
        sections.failedUpdate(ssid, edPanelId);
    }
    public void cycleUpdateFailed(long ssid, long edPanelId) {cycles.failedUpdate(ssid, edPanelId);}
    public void periodUpdateFailed(long ssid, long edPanelId) {periods.failedUpdate(ssid, edPanelId);}
    public void sectionAssignmentUpdateFailed(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.failedUpdate(ssid, edPanelId);
    }
    public void studentAssignmentUpdateFailed(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).failedUpdate(ssid, edPanelId);
    }
    public void studentSectionGradeUpdateFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.failedUpdate(ssid, edPanelId);
    }
    /*
        UPDATE HELPER METHODS
    */
    public void schoolDayUpdated(long ssid, long edPanelId) {
        schoolDays.updated(ssid, edPanelId);
    }
    public void attendanceUpdated(long ssid, long edPanelId) {
        attendance.updated(ssid, edPanelId);
    }
    public void schoolUpdated(long ssid, long edPanelId) {
        schools.updated(ssid, edPanelId);
    }
    public void yearUpdated(long ssid, long edPanelId) {
        schoolYears.updated(ssid, edPanelId);
    }
    public void termUpdated(long ssid, long edPanelId) {
        terms.updated(ssid, edPanelId);
    }
    public void courseUpdated(long ssid, long edPanelId) {
        courses.updated(ssid, edPanelId);
    }
    public void studentUpdated(long ssid, long edPanelId) {
        students.updated(ssid, edPanelId);
    }
    public void staffUpdated(long ssid, long edPanelId) {
        staff.updated(ssid, edPanelId);
    }
    public void sectionUpdated(long ssid, long edPanelId) {
        sections.updated(ssid, edPanelId);
    }
    public void cycleUpdated(long ssid, long edPanelId) {sections.updated(ssid, edPanelId);}
    public void periodUpdated(long ssid, long edPanelId) {periods.updated(ssid, edPanelId);}
    public void sectionAssignmentUpdated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.updated(ssid, edPanelId);
    }
    public void studentAssignmentUpdated(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).updated(ssid, edPanelId);
    }
    public void studentSectionGradeUpdated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.updated(ssid, edPanelId);
    }
    /*
        FAILED DELETE HELPER METHODS
     */
    public void schoolDayDeleteFailed(long ssid, long edPanelId) {
        schoolDays.failedDelete(ssid, edPanelId);
    }
    public void attendanceDeleteFailed(long ssid, long edPanelId) {
        attendance.failedDelete(ssid, edPanelId);
    }
    public void schoolDeleteFailed(long ssid, long edPanelId) {
        schools.failedDelete(ssid, edPanelId);
    }
    public void yearDeleteFailed(long ssid, long edPanelId) {
        schoolYears.failedDelete(ssid, edPanelId);
    }
    public void termDeleteFailed(long ssid, long edPanelId) {
        terms.failedDelete(ssid, edPanelId);
    }
    public void courseDeleteFailed(long ssid, long edPanelId) {
        courses.failedDelete(ssid, edPanelId);
    }
    public void studentDeleteFailed(long ssid, long edPanelId) {
        students.failedDelete(ssid, edPanelId);
    }
    public void staffDeleteFailed(long ssid, long edPanelId) {
        staff.failedDelete(ssid, edPanelId);
    }
    public void sectionDeleteFailed(long ssid, long edPanelId) {
        sections.failedDelete(ssid, edPanelId);
    }
    public void cycleDeleteFailed(long ssid, long edPanelId) { cycles.failedDelete(ssid, edPanelId);}
    public void periodDeleteFailed(long ssid, long edPanelId) { periods.failedDelete(ssid, edPanelId);}
    public void sectionAssignmentDeleteFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.failedDelete(ssid, edPanelId);
    }
    public void studentAssignmentDeleteFailed(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).failedDelete(ssid, edPanelId);
    }
    public void studentSectionGradeDeleteFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.failedDelete(ssid, edPanelId);
    }
    /*
        DELETE HELPER METHODS
     */
    public void schoolDayDeleted(long ssid, long edPanelId) {
        schoolDays.deleted(ssid, edPanelId);
    }
    public void attendanceDeleted(long ssid, long edPanelId) {
        attendance.deleted(ssid, edPanelId);
    }
    public void schoolDeleted(long ssid, long edPanelId) {
        schools.deleted(ssid, edPanelId);
    }
    public void yearDeleted(long ssid, long edPanelId) {
        schoolYears.deleted(ssid, edPanelId);
    }
    public void termDeleted(long ssid, long edPanelId) {
        terms.deleted(ssid, edPanelId);
    }
    public void courseDeleted(long ssid, long edPanelId) {
        courses.deleted(ssid, edPanelId);
    }
    public void studentDeleted(long ssid, long edPanelId) {
        students.deleted(ssid, edPanelId);
    }
    public void staffDeleted(long ssid, long edPanelId) {
        staff.deleted(ssid, edPanelId);
    }
    public void sectionDeleted(long ssid, long edPanelId) {
        sections.deleted(ssid, edPanelId);
    }
    public void cycleDeleted(long ssid, long edPanelId) {cycles.deleted(ssid, edPanelId);}
    public void periodDeleted(long ssid, long edPanelId) {periods.deleted(ssid, edPanelId);}
    public void sectionAssignmentDeleted(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.deleted(ssid, edPanelId);
    }
    public void studentAssignmentDeleted(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).deleted(ssid, edPanelId);
    }
    public void studentSectionGradeDeleted(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.deleted(ssid, edPanelId);
    }

    /*
    UNTOUCHED HELPER METHODS
    */
    public void schoolDayUntouched(long ssid, long edPanelId) {
        schoolDays.untouched(ssid, edPanelId);
    }
    public void attendanceUntouched(long ssid, long edPanelId) {
        attendance.untouched(ssid, edPanelId);
    }
    public void schoolUntouched(long ssid, long edPanelId) {
        schools.untouched(ssid, edPanelId);
    }
    public void yearUntouched(long ssid, long edPanelId) {
        schoolYears.untouched(ssid, edPanelId);
    }
    public void termUntouched(long ssid, long edPanelId) {
        terms.untouched(ssid, edPanelId);
    }
    public void courseUntouched(long ssid, long edPanelId) {
        courses.untouched(ssid, edPanelId);
    }
    public void studentUntouched(long ssid, long edPanelId) {
        students.untouched(ssid, edPanelId);
    }
    public void staffUntouched(long ssid, long edPanelId) {
        staff.untouched(ssid, edPanelId);
    }
    public void sectionUntouched(long ssid, long edPanelId) {
        sections.untouched(ssid, edPanelId);
    }
    public void cycleUntouched(long ssid, long edPanelId) {cycles.untouched(ssid, edPanelId);}
    public void periodUntouched(long ssid, long edPanelId) {periods.untouched(ssid, edPanelId);}
    public void sectionAssignmentUntouched(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.untouched(ssid, edPanelId);
    }
    public void studentAssignmentUntouched(long sectionssid, long assignmentssid, long ssid, long edPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).untouched(ssid, edPanelId);
    }
    public void studentSectionGradeUntouched(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.untouched(ssid, edPanelId);
    }

    /*
        GET ALL FROM SOURCE SYSTEM FAILED HELPER METHODS
     */
    public void schoolDaySourceGetFailed(long ssid, long edPanelId) {
        schoolDays.getSourceGetFailed().put(ssid, edPanelId);
    }
    public void attendanceSourceGetFailed(long ssid, long edPanelId) {
        attendance.getSourceGetFailed().put(ssid, edPanelId);
    }
    public void schoolSourceGetFailed(long parentSsid, long parentEdPanelId) {
        schools.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void yearSourceGetFailed(long parentSsid, long parentEdPanelId) {
        schoolYears.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void termSourceGetFailed(long parentSsid, long parentEdPanelId) {
        terms.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void courseSourceGetFailed(long parentSsid, long parentEdPanelId) {
        courses.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSourceGetFailed(long parentSsid, long parentEdPanelId) {
        students.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void staffSourceGetFailed(long parentSsid, long parentEdPanelId) {
        staff.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void sectionSourceGetFailed(long parentSsid, long parentEdPanelId) {
        sections.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void cycleSourceGetFailed(long parentSsid, long parentEdPanelId) {
        cycles.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void periodSourceGetFailed(long parentSsid, long parentEdPanelId) {
        periods.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void sectionAssignmentSourceGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentAssignmentSourceGetFailed(long sectionssid,
                                                 long assignmentssid,
                                                 long parentSsid,
                                                 long parentEdPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSectionGradeSourceGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }

    /*
        GET ALL FROM EDPANEL FAILED HELPER METHODS
    */
    public void schoolDayEdPaneleGetFailed(long ssid, long edPanelId) {
        schoolDays.getEdPanelGetFailed().put(ssid, edPanelId);
    }
    public void attendanceEdPanelGetFailed(long ssid, long edPanelId) {
        attendance.getEdPanelGetFailed().put(ssid, edPanelId);
    }
    public void schoolEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        schools.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void yearEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        schoolYears.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void termEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        terms.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void courseEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        courses.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        students.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void staffEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        staff.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void sectionEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        sections.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }

    public void sectionAssignmentEdPanelGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            sectionAssignments.put(sectionssid, result);
        }
        result.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentAssignmentEdPanelGetFailed(long sectionssid,
                                                  long assignmentssid,
                                                  long parentSsid,
                                                  long parentEdPanelId) {
        ConcurrentHashMap<Long, EntitySyncResult> result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new ConcurrentHashMap<>();
            studentAssignments.put(sectionssid, result);
        }
        if(null == result.get(assignmentssid)) {
            result.put(assignmentssid, new EntitySyncResult());
        }
        result.get(assignmentssid).getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSectionGradeEdPanelGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
            studentSectionGrades.put(sectionssid, result);
        }
        result.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }


    /*
        GETTERS & SETTERS
     */
    public EntitySyncResult getSchools() {
        return schools;
    }
    public void setSchools(EntitySyncResult schools) {
        this.schools = schools;
    }
    public EntitySyncResult getSchoolYears() {
        return schoolYears;
    }
    public void setSchoolYears(EntitySyncResult schoolYears) {
        this.schoolYears = schoolYears;
    }
    public EntitySyncResult getTerms() {
        return terms;
    }
    public void setTerms(EntitySyncResult terms) {
        this.terms = terms;
    }
    public EntitySyncResult getCourses() {
        return courses;
    }
    public void setCourses(EntitySyncResult courses) {
        this.courses = courses;
    }
    public EntitySyncResult getStudents() {
        return students;
    }
    public void setStudents(EntitySyncResult students) {
        this.students = students;
    }
    public EntitySyncResult getStaff() {
        return staff;
    }
    public void setStaff(EntitySyncResult staff) {
        this.staff = staff;
    }
    public EntitySyncResult getSections() {
        return sections;
    }
    public void setSections(EntitySyncResult sections) {
        this.sections = sections;
    }
    public ConcurrentHashMap<Long, EntitySyncResult> getSectionAssignments() {
        return sectionAssignments;
    }
    public void setSectionAssignments(ConcurrentHashMap<Long, EntitySyncResult> sectionAssignments) {
        this.sectionAssignments = sectionAssignments;
    }
    public ConcurrentHashMap<Long, ConcurrentHashMap<Long, EntitySyncResult>> getStudentAssignments() {
        return studentAssignments;
    }
    public void setStudentAssignments(
            ConcurrentHashMap<Long, ConcurrentHashMap<Long, EntitySyncResult>> studentAssignments) {
        this.studentAssignments = studentAssignments;
    }
    public ConcurrentHashMap<Long, EntitySyncResult> getStudentSectionGrades() {
        return studentSectionGrades;
    }
    public void setStudentSectionGrades(ConcurrentHashMap<Long, EntitySyncResult> studentSectionGrades) {
        this.studentSectionGrades = studentSectionGrades;
    }

    @Override
    public String getResultString() { 
        return getResultString(this);
    }
    
    private static void appendWithNewLine(StringBuilder builder, String string) { 
        builder.append(string);
        builder.append("\n");
    } 
    
    // this overloaded method ONLY prints the line if the size isn't zero
    private static void appendWithNewLine(StringBuilder builder, String label, int size) { 
        if (size != 0) {
            appendWithNewLine(builder, label + size);
        }
    }
    
    private static String getResultString(PowerSchoolSyncResult results) {
        StringBuilder output = new StringBuilder();
        appendWithNewLine(output, "");
        appendWithNewLine(output, "-----------------------");
        appendWithNewLine(output, " SUCCESSFUL OPERATIONS ");
        appendWithNewLine(output, "-----------------------");
        appendSucceeded(output, "Schools", results.getSchools());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "School years", results.getSchoolYears());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "Courses", results.getCourses());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "Terms", results.getTerms());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "staff", results.getStaff());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "students", results.getStudents());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "sections", results.getSections());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "section assignments", results.getSectionAssignments());
        appendWithNewLine(output, "--");
        appendSucceeded(output, "section student grades", results.getStudentSectionGrades());
        appendWithNewLine(output, "--");

        Integer created = 0;
        Integer updated = 0;
        Integer deleted = 0;
        Integer untouched = 0;
        Integer failedSourceGets = 0;
        Integer failedEdPanelGets = 0;
        Integer failedCreates = 0;
        Integer failedUpdates = 0;
        Integer failedDeletes = 0;
        for(Map.Entry<Long, ConcurrentHashMap<Long, EntitySyncResult>> sa : results.getStudentAssignments().entrySet()) {
            for(Map.Entry<Long, EntitySyncResult> a : sa.getValue().entrySet()) {
                created += a.getValue().getCreated().size();
                updated += a.getValue().getUpdated().size();
                deleted += a.getValue().getDeleted().size();
                untouched += a.getValue().getUntouched().size();
                failedSourceGets += a.getValue().getSourceGetFailed().size();
                failedEdPanelGets += a.getValue().getEdPanelGetFailed().size();
                failedCreates += a.getValue().getFailedCreates().size();
                failedUpdates += a.getValue().getFailedUpdates().size();
                failedDeletes += a.getValue().getFailedDeletes().size();
            }
        }
        appendWithNewLine(output, "Created student assignments: ", created);
        appendWithNewLine(output, "Updated student assignments: ", updated);
        appendWithNewLine(output, "Deleted student assignments: ", deleted);
        appendWithNewLine(output, "Untouched student assignments: ", untouched);

        appendWithNewLine(output, "-----------------------");
        appendWithNewLine(output, "   FAILED OPERATIONS   ");
        appendWithNewLine(output, "-----------------------");
        appendFailed(output, "school", results.getSchools());
        appendWithNewLine(output, "--");
        appendFailed(output, "school year", results.getSchoolYears());
        appendWithNewLine(output, "--");
        appendFailed(output, "course", results.getCourses());
        appendWithNewLine(output, "--");
        appendFailed(output, "term", results.getTerms());
        appendWithNewLine(output, "--");
        appendFailed(output, "staff", results.getStaff());
        appendWithNewLine(output, "--");
        appendFailed(output, "student", results.getStudents());
        appendWithNewLine(output, "--");
        appendFailed(output, "sections", results.getSections());
        appendWithNewLine(output, "--");
        appendFailed(output, "section assignments", results.getSectionAssignments());
        appendWithNewLine(output, "--");
        appendFailed(output, "section student grades", results.getStudentSectionGrades());
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Failed student assignments source gets: ", failedSourceGets);
        appendWithNewLine(output, "Failed student assignments edpanel gets: ", failedEdPanelGets);
        appendWithNewLine(output, "Failed student assignments creations: ", failedCreates);
        appendWithNewLine(output, "Failed student assignments updates: ", failedUpdates);
        appendWithNewLine(output, "Failed student assignments deletes: ", failedDeletes);

        return output.toString();
    }

    private static void appendSucceeded(StringBuilder output, String entityName, EntitySyncResult entity) {
        appendSucceeded(output, entityName, toSuccessRecord(new SuccessRecord(), entity));
    }

    private static void appendFailed(StringBuilder output, String entityName, EntitySyncResult entity) {
        appendFailed(output, entityName, toFailureRecord(new FailureRecord(), entity));
    }

    private static void appendSucceeded(StringBuilder output, String entityName, Map<Long, EntitySyncResult> entities) {
        SuccessRecord successRecord = new SuccessRecord();
        for(Map.Entry<Long, EntitySyncResult> a : entities.entrySet()) {
            toSuccessRecord(successRecord, a.getValue());
        }
        appendSucceeded(output, entityName, successRecord);
    }
    
    private static void appendFailed(StringBuilder output, String entityName, Map<Long, EntitySyncResult> entities) {
        FailureRecord failureRecord = new FailureRecord();
        for(Map.Entry<Long, EntitySyncResult> a : entities.entrySet()) {
            toFailureRecord(failureRecord, a.getValue());
        }
        appendFailed(output, entityName, failureRecord);
    }
    
    private static void appendSucceeded(StringBuilder output, String entityName, 
                                        SuccessRecord successRecord) {
        appendWithNewLine(output, "Created " + entityName + ": ", successRecord.created);
        appendWithNewLine(output, "Updated " + entityName + ": ", successRecord.updated);
        appendWithNewLine(output, "Deleted " + entityName + ": ", successRecord.deleted);
        appendWithNewLine(output, "Untouched " + entityName + ": ", successRecord.untouched);
    }

    private static void appendFailed(StringBuilder output, String entityName,
                                     FailureRecord failureRecord) {
        appendWithNewLine(output, "Failed " + entityName + " source gets: ", failureRecord.sourceGetFailed);
        appendWithNewLine(output, "Failed " + entityName + " edpanel gets: ", failureRecord.edPanelGetFailed);
        appendWithNewLine(output, "Failed " + entityName + " creations: ", failureRecord.failedCreates);
        appendWithNewLine(output, "Failed " + entityName + " updates: ", failureRecord.failedUpdates);
        appendWithNewLine(output, "Failed " + entityName + " deletes: ", failureRecord.failedDeletes);
    }
    
    private static SuccessRecord toSuccessRecord(SuccessRecord existingRecord, EntitySyncResult entity) {
        existingRecord.created += entity.getCreated().size();
        existingRecord.updated += entity.getUpdated().size();
        existingRecord.deleted += entity.getDeleted().size();
        existingRecord.untouched += entity.getUntouched().size();
        return existingRecord;
    }

    private static FailureRecord toFailureRecord(FailureRecord existingRecord, EntitySyncResult entity) {
        existingRecord.sourceGetFailed += entity.getSourceGetFailed().size();
        existingRecord.edPanelGetFailed += entity.getEdPanelGetFailed().size();
        existingRecord.failedCreates += entity.getFailedCreates().size();
        existingRecord.failedUpdates += entity.getFailedUpdates().size();
        existingRecord.failedDeletes += entity.getFailedDeletes().size();
        return existingRecord;
    }

    private static class SuccessRecord {
        int created = 0;
        int updated = 0;
        int deleted = 0;
        int untouched = 0;
    }
    
    private static class FailureRecord { 
        int sourceGetFailed = 0;
        int edPanelGetFailed = 0;
        int failedCreates = 0;
        int failedUpdates = 0;
        int failedDeletes = 0;
    }
}
