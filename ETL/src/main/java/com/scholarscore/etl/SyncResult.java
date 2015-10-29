package com.scholarscore.etl;

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
    protected EntitySyncResult sectionAssignments = new EntitySyncResult();
    protected EntitySyncResult studentAssignments = new EntitySyncResult();
    protected EntitySyncResult studentSectionGrades = new EntitySyncResult();
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
    public void sectionAssignmentCreateFailed(long ssid) {
        sectionAssignments.failedCreate(ssid);
    }
    public void studentAssignmentCreateFailed(long ssid) {
        studentAssignments.failedCreate(ssid);
    }
    public void studentSectionGradeCreateFailed(long ssid) {
        studentSectionGrades.failedCreate(ssid);
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
    public void sectionAssignmentCreated(long ssid, long edPanelId) {
        sectionAssignments.created(ssid, edPanelId);
    }
    public void studentAssignmentCreated(long ssid, long edPanelId) {
        studentAssignments.created(ssid, edPanelId);
    }
    public void studentSectionGradeCreated(long ssid, long edPanelId) {
        studentSectionGrades.created(ssid, edPanelId);
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
    public void sectionAssignmentUpdateFailed(long ssid, long edPanelId) {
        sectionAssignments.failedUpdate(ssid, edPanelId);
    }
    public void studentAssignmentUpdateFailed(long ssid, long edPanelId) {
        studentAssignments.failedUpdate(ssid, edPanelId);
    }
    public void studentSectionGradeUpdateFailed(long ssid, long edPanelId) {
        studentSectionGrades.failedUpdate(ssid, edPanelId);
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
    public void sectionAssignmentUpdated(long ssid, long edPanelId) {
        sectionAssignments.updated(ssid, edPanelId);
    }
    public void studentAssignmentUpdated(long ssid, long edPanelId) {
        studentAssignments.updated(ssid, edPanelId);
    }
    public void studentSectionGradeUpdated(long ssid, long edPanelId) {
        studentSectionGrades.updated(ssid, edPanelId);
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
    public void sectionAssignmentDeleteFailed(long ssid, long edPanelId) {
        sectionAssignments.failedDelete(ssid, edPanelId);
    }
    public void studentAssignmentDeleteFailed(long ssid, long edPanelId) {
        studentAssignments.deleted(ssid, edPanelId);
    }
    public void studentSectionGradeDeleteFailed(long ssid, long edPanelId) {
        studentSectionGrades.failedDelete(ssid, edPanelId);
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
    public void sectionAssignmentDeleted(long ssid, long edPanelId) {
        sectionAssignments.deleted(ssid, edPanelId);
    }
    public void studentAssignmentDeleted(long ssid, long edPanelId) {
        studentAssignments.deleted(ssid, edPanelId);
    }
    public void studentSectionGradeDeleted(long ssid, long edPanelId) {
        studentSectionGrades.deleted(ssid, edPanelId);
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
    public void sectionAssignmentSourceGetFailed(long parentSsid, long parentEdPanelId) {
        sectionAssignments.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentAssignmentSourceGetFailed(long parentSsid, long parentEdPanelId) {
        studentAssignments.getSourceGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSectionGradeSourceGetFailed(long parentSsid, long parentEdPanelId) {
        studentSectionGrades.getSourceGetFailed().put(parentSsid, parentEdPanelId);
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
    public void sectionAssignmentEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        sectionAssignments.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentAssignmentEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        studentAssignments.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
    }
    public void studentSectionGradeEdPanelGetFailed(long parentSsid, long parentEdPanelId) {
        studentSectionGrades.getEdPanelGetFailed().put(parentSsid, parentEdPanelId);
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
    public EntitySyncResult getSectionAssignments() {
        return sectionAssignments;
    }
    public void setSectionAssignments(EntitySyncResult sectionAssignments) {
        this.sectionAssignments = sectionAssignments;
    }
    public EntitySyncResult getStudentAssignments() {
        return studentAssignments;
    }
    public void setStudentAssignments(EntitySyncResult studentAssignments) {
        this.studentAssignments = studentAssignments;
    }
    public EntitySyncResult getStudentSectionGrades() {
        return studentSectionGrades;
    }
    public void setStudentSectionGrades(EntitySyncResult studentSectionGrades) {
        this.studentSectionGrades = studentSectionGrades;
    }
}
