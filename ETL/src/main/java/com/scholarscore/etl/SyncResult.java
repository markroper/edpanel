package com.scholarscore.etl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/29/15.
 */
public class SyncResult {
    protected EntitySyncResult schools = new EntitySyncResult();
    protected EntitySyncResult schoolYears = new EntitySyncResult();
    protected EntitySyncResult terms = new EntitySyncResult();
    protected EntitySyncResult courses = new EntitySyncResult();
    protected EntitySyncResult students = new EntitySyncResult();
    protected EntitySyncResult staff = new EntitySyncResult();
    protected EntitySyncResult sections = new EntitySyncResult();

    protected ConcurrentHashMap<Long, EntitySyncResult> sectionAssignments =
            new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, EntitySyncResult> studentAssignments =
            new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, EntitySyncResult> studentSectionGrades =
            new ConcurrentHashMap<>();
    /*
        CREATE FAILED HELPER METHODS
    */
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
    public void sectionAssignmentCreateFailed(long sectionssid, long ssid) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
           result = new EntitySyncResult();
        }
        result.failedCreate(ssid);
    }
    public void studentAssignmentCreateFailed(long sectionssid, long ssid) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedCreate(ssid);
    }
    public void studentSectionGradeCreateFailed(long sectionssid, long ssid) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedCreate(ssid);
    }
    /*
        CREATE HELPER METHODS
    */
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
    public void sectionAssignmentCreated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.created(ssid, edPanelId);
    }
    public void studentAssignmentCreated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.created(ssid, edPanelId);
    }
    public void studentSectionGradeCreated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.created(ssid, edPanelId);
    }
    /*
        UPDATE FAILED HELPER METHODS
    */
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
    public void sectionAssignmentUpdateFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedUpdate(ssid, edPanelId);
    }
    public void studentAssignmentUpdateFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedUpdate(ssid, edPanelId);
    }
    public void studentSectionGradeUpdateFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedUpdate(ssid, edPanelId);
    }
    /*
        UPDATE HELPER METHODS
    */
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
    public void sectionAssignmentUpdated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.updated(ssid, edPanelId);
    }
    public void studentAssignmentUpdated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.updated(ssid, edPanelId);
    }
    public void studentSectionGradeUpdated(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.updated(ssid, edPanelId);
    }
    /*
        FAILED DELETE HELPER METHODS
     */
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
    public void sectionAssignmentDeleteFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedDelete(ssid, edPanelId);
    }
    public void studentAssignmentDeleteFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedDelete(ssid, edPanelId);
    }
    public void studentSectionGradeDeleteFailed(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.failedDelete(ssid, edPanelId);
    }
    /*
        DELETE HELPER METHODS
     */
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
    public void sectionAssignmentDeleted(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.deleted(ssid, edPanelId);
    }
    public void studentAssignmentDeleted(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.deleted(ssid, edPanelId);
    }
    public void studentSectionGradeDeleted(long sectionssid, long ssid, long edPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.deleted(ssid, edPanelId);
    }

    /*
        GET ALL FROM SOURCE SYSTEM FAILED HELPER METHODS
     */
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
    public void sectionAssignmentSourceGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = sectionAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentAssignmentSourceGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSectionGradeSourceGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }

    /*
        GET ALL FROM EDPANEL FAILED HELPER METHODS
    */
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
        }
        result.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentAssignmentEdPanelGetFailed(long sectionssid,long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = studentAssignments.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
        }
        result.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSectionGradeEdPanelGetFailed(long sectionssid, long parentSsid, long parentEdPanelId) {
        EntitySyncResult result = studentSectionGrades.get(sectionssid);
        if(null == result) {
            result = new EntitySyncResult();
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
    public ConcurrentHashMap<Long, EntitySyncResult> getStudentAssignments() {
        return studentAssignments;
    }
    public void setStudentAssignments(ConcurrentHashMap<Long, EntitySyncResult> studentAssignments) {
        this.studentAssignments = studentAssignments;
    }
    public ConcurrentHashMap<Long, EntitySyncResult> getStudentSectionGrades() {
        return studentSectionGrades;
    }
    public void setStudentSectionGrades(ConcurrentHashMap<Long, EntitySyncResult> studentSectionGrades) {
        this.studentSectionGrades = studentSectionGrades;
    }
}
