package com.scholarscore.api.manager;

/**
 * This orchestration manager class contains a reference to the manager class for each entity in 
 * the API.  The purpose of the class is to provide one clearing house for the implementation of the 
 * manager for a particular resource. Individual managers may themselves maintain a reference to this
 * orchestration manager so that they can interrogate it for the manager instance for dependent entities.
 * 
 * @author markroper
 *
 */
public class OrchestrationManager {

    //Manager references for each Entity
    private SchoolManager schoolManager;
    private SchoolYearManager schoolYearManager;
    private TermManager termManager;
    private StudentManager studentManager;
    private TeacherManager teacherManager;
    private AdminManager adminManager;
    private SectionManager sectionManager;
    private CourseManager courseManager;
    private AssignmentManager assignmentManager;
    private StudentAssignmentManager studentAssignmentManager;
    private StudentSectionGradeManager studentSectionGradeManager;
    private UserManager userManager;
    private QueryManager queryManager;
    private BehaviorManager behaviorManager;
    private GoalManager goalManager;
    private SchoolDayManager schoolDayManager;
    private AttendanceManager attendanceManager;
    private UiAttributesManager uiAttributesManager;

    
    public UiAttributesManager getUiAttributesManager() {
        return uiAttributesManager;
    }

    public void setUiAttributesManager(UiAttributesManager uiAttributesManager) {
        this.uiAttributesManager = uiAttributesManager;
    }

    public SchoolManager getSchoolManager() {
        return schoolManager;
    }

    public void setSchoolManager(SchoolManager schoolManager) {
        this.schoolManager = schoolManager;
    }

    public SchoolYearManager getSchoolYearManager() {
        return schoolYearManager;
    }

    public void setSchoolYearManager(SchoolYearManager schoolYearManager) {
        this.schoolYearManager = schoolYearManager;
    }

    public TermManager getTermManager() {
        return termManager;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public void setGoalManager(GoalManager goalManager) {
        this.goalManager = goalManager;
    }

    public void setTermManager(TermManager termManager) {
        this.termManager = termManager;
    }

    public TeacherManager getTeacherManager() {
        return teacherManager;
    }

    public void setTeacherManager(TeacherManager teacherManager) {
        this.teacherManager = teacherManager;
    }

    public StudentManager getStudentManager() {
        return studentManager;
    }

    public void setStudentManager(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }

    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    public SectionManager getSectionManager() {
        return sectionManager;
    }

    public void setSectionManager(SectionManager sectionManager) {
        this.sectionManager = sectionManager;
    }

    public AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    public void setAssignmentManager(AssignmentManager assignmentManager) {
        this.assignmentManager = assignmentManager;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    public StudentAssignmentManager getStudentAssignmentManager() {
        return studentAssignmentManager;
    }

    public void setStudentAssignmentManager(StudentAssignmentManager studentAssignmentManager) {
        this.studentAssignmentManager = studentAssignmentManager;
    }

    public StudentSectionGradeManager getStudentSectionGradeManager() {
        return studentSectionGradeManager;
    }

    public void setStudentSectionGradeManager(StudentSectionGradeManager studentSectionGradeManager) {
        this.studentSectionGradeManager = studentSectionGradeManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
    
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public QueryManager getQueryManager() {
        return queryManager;
    }

    public void setQueryManager(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    public BehaviorManager getBehaviorManager() {
        return behaviorManager;
    }

    public void setBehaviorManager(BehaviorManager behaviorManager) {
        this.behaviorManager = behaviorManager;
    }
    
    public SchoolDayManager getSchoolDayManager() {
        return schoolDayManager;
    }

    public void setSchoolDayManager(SchoolDayManager schoolDayManager) {
        this.schoolDayManager = schoolDayManager;
    }

    public AttendanceManager getAttendanceManager() {
        return attendanceManager;
    }

    public void setAttendanceManager(AttendanceManager attendanceManager) {
        this.attendanceManager = attendanceManager;
    }
}
