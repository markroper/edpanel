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
    public void cycleCreateFailed(long ssid) { cycles.failedCreate(ssid);}
    public void periodCreateFailed(long ssid) { periods.failedCreate(ssid);}
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
            result = new ConcurrentHashMap<Long, EntitySyncResult>();
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
            result = new ConcurrentHashMap<Long, EntitySyncResult>();
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
            result = new ConcurrentHashMap<Long, EntitySyncResult>();
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
            result = new ConcurrentHashMap<Long, EntitySyncResult>();
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
            result = new ConcurrentHashMap<Long, EntitySyncResult>();
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
    
    private static String getResultString(PowerSchoolSyncResult results) {
        StringBuilder output = new StringBuilder();
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Created Schools: " + results.getSchools().getCreated().size());
        appendWithNewLine(output, "Failed school creations: " + results.getSchools().getFailedCreates().size());
        appendWithNewLine(output, "Failed school source gets: " + results.getSchools().getSourceGetFailed().size());
        appendWithNewLine(output, "Failed school edpanel gets: " + results.getSchools().getEdPanelGetFailed().size());
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Created Courses: " + results.getCourses().getCreated().size());
        appendWithNewLine(output, "Updated Courses: " + results.getCourses().getUpdated().size());
        appendWithNewLine(output, "Failed courses creations: " + results.getCourses().getFailedCreates().size());
        appendWithNewLine(output, "Failed courses source gets: " + results.getCourses().getSourceGetFailed().size());
        appendWithNewLine(output, "Failed courses edpanel gets: " + results.getCourses().getEdPanelGetFailed().size());
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Created Terms: " + results.getTerms().getCreated().size());
        appendWithNewLine(output, "Updated Terms: " + results.getTerms().getUpdated().size());
        appendWithNewLine(output, "Failed terms creations: " + results.getTerms().getFailedCreates().size());
        appendWithNewLine(output, "Failed terms source gets: " + results.getTerms().getSourceGetFailed().size());
        appendWithNewLine(output, "Failed terms edpanel gets: " + results.getTerms().getEdPanelGetFailed().size());
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Created staff: " + results.getStaff().getCreated().size());
        appendWithNewLine(output, "Updated staff: " + results.getStaff().getUpdated().size());
        appendWithNewLine(output, "Failed staff creations: " + results.getStaff().getFailedCreates().size());
        appendWithNewLine(output, "Failed staff source gets: " + results.getStaff().getSourceGetFailed().size());
        appendWithNewLine(output, "Failed staff edpanel gets: " + results.getStaff().getEdPanelGetFailed().size());
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Created students: " + results.getStudents().getCreated().size());
        appendWithNewLine(output, "Updated students: " + results.getStudents().getUpdated().size());
        appendWithNewLine(output, "Deleted students: " + results.getStudents().getDeleted().size());
        appendWithNewLine(output, "Failed students creations: " + results.getStudents().getFailedCreates().size());
        appendWithNewLine(output, "Failed students source gets: " + results.getStudents().getSourceGetFailed().size());
        appendWithNewLine(output, "Failed students edpanel gets: " + results.getStudents().getEdPanelGetFailed().size());
        appendWithNewLine(output, "--");
        appendWithNewLine(output, "Created sections: " + results.getSections().getCreated().size());
        appendWithNewLine(output, "Updated sections: " + results.getSections().getUpdated().size());
        appendWithNewLine(output, "Deleted sections: " + results.getSections().getDeleted().size());
        appendWithNewLine(output, "Failed sections creations: " + results.getSections().getFailedCreates().size());
        appendWithNewLine(output, "Failed sections source gets: " + results.getSections().getSourceGetFailed().size());
        appendWithNewLine(output, "Failed sections edpanel gets: " + results.getSections().getEdPanelGetFailed().size());
        appendWithNewLine(output, "--");
        Integer studAssignments = 0;
        Integer studUpdatedAssignments = 0;
        Integer studDeletedAssignments = 0;
        Integer studAssFailedCreates = 0;
        Integer studAssFailedSourceGets = 0;
        Integer studAssFailedEdPanelGets = 0;
        for(Map.Entry<Long, EntitySyncResult> sa : results.getSectionAssignments().entrySet()) {
            studAssignments += sa.getValue().getCreated().size();
            studUpdatedAssignments += sa.getValue().getUpdated().size();
            studDeletedAssignments += sa.getValue().getDeleted().size();
            studAssFailedCreates += sa.getValue().getFailedCreates().size();
            studAssFailedSourceGets += sa.getValue().getSourceGetFailed().size();
            studAssFailedEdPanelGets += sa.getValue().getEdPanelGetFailed().size();
        }
        appendWithNewLine(output, "Created section assignments: " + studAssignments);
        appendWithNewLine(output, "Updated section assignments: " + studUpdatedAssignments);
        appendWithNewLine(output, "Deleted section assignments: " + studDeletedAssignments);
        appendWithNewLine(output, "Failed section assignments creations: " + studAssFailedCreates);
        appendWithNewLine(output, "Failed section assignments source gets: " + studAssFailedSourceGets);
        appendWithNewLine(output, "Failed section assignments edpanel gets: " + studAssFailedEdPanelGets);
        appendWithNewLine(output, "--");

        Integer ssgs = 0;
        Integer ssgsUpdated = 0;
        Integer ssgsDeleted = 0;
        Integer ssgFailedCreates = 0;
        Integer ssgFailedSourceGets = 0;
        Integer ssgFailedEdPanelGets = 0;
        for(Map.Entry<Long, EntitySyncResult> sa : results.getStudentSectionGrades().entrySet()) {
            ssgs += sa.getValue().getCreated().size();
            ssgsUpdated += sa.getValue().getUpdated().size();
            ssgsDeleted += sa.getValue().getDeleted().size();
            ssgFailedCreates += sa.getValue().getFailedCreates().size();
            ssgFailedSourceGets += sa.getValue().getSourceGetFailed().size();
            ssgFailedEdPanelGets += sa.getValue().getEdPanelGetFailed().size();
        }
        appendWithNewLine(output, "Created section student grades: " + ssgs);
        appendWithNewLine(output, "Updated section student grades: " + ssgsUpdated);
        appendWithNewLine(output, "Deleted section student grades: " + ssgsDeleted);
        appendWithNewLine(output, "Failed ssg creations: " + ssgFailedCreates);
        appendWithNewLine(output, "Failed ssg source gets: " + ssgFailedSourceGets);
        appendWithNewLine(output, "Failed ssg edpanel gets: " + ssgFailedEdPanelGets);
        appendWithNewLine(output, "--");
        Integer sectAss = 0;
        Integer sectAssUpdated = 0;
        Integer sectAssDeleted = 0;
        Integer sectAssFailedCreates = 0;
        Integer sectAssFailedSourceGets = 0;
        Integer sectAssFailedEdPanelGets = 0;
        for(Map.Entry<Long, ConcurrentHashMap<Long, EntitySyncResult>> sa : results.getStudentAssignments().entrySet()) {
            for(Map.Entry<Long, EntitySyncResult> a : sa.getValue().entrySet()) {
                sectAss += a.getValue().getCreated().size();
                sectAssUpdated += a.getValue().getUpdated().size();
                sectAssDeleted += a.getValue().getDeleted().size();
                sectAssFailedCreates += a.getValue().getFailedCreates().size();
                sectAssFailedSourceGets += a.getValue().getSourceGetFailed().size();
                sectAssFailedEdPanelGets += a.getValue().getEdPanelGetFailed().size();
            }
        }
        appendWithNewLine(output, "Created student assignments: " + sectAss);
        appendWithNewLine(output, "Updated student assignments: " + sectAssUpdated);
        appendWithNewLine(output, "Deleted student assignments: " + sectAssDeleted);
        appendWithNewLine(output, "Failed student assignments creations: " + sectAssFailedCreates);
        appendWithNewLine(output, "Failed student assignments source gets: " + sectAssFailedSourceGets);
        appendWithNewLine(output, "Failed student assignments edpanel gets: " + sectAssFailedEdPanelGets);
        return output.toString();
    }
}
