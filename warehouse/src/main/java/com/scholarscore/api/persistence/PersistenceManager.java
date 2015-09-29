package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.*;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;



public class PersistenceManager
         {

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
    
    //Persistence managers for each entity
    private EntityPersistence<Assignment> assignmentPersistence;
    private SchoolPersistence schoolPersistence;
    private EntityPersistence<SchoolYear> schoolYearPersistence;
    private EntityPersistence<Term> termPersistence;
    private StudentPersistence studentPersistence;
    private TeacherPersistence teacherPersistence;
    private AdministratorPersistence administratorPersistence;
    private SectionPersistence sectionPersistence;
    private EntityPersistence<Course> coursePersistence;
    private StudentAssignmentPersistence studentAssignmentPersistence;
    private StudentSectionGradePersistence studentSectionGradePersistence;
    private UserPersistence userPersistence;
    private AuthorityPersistence authorityPersistence;
    private QueryPersistence queryPersistence;
    private BehaviorPersistence behaviorPersistence;
    private SchoolDayPersistence schoolDayPersistence;
    private AttendancePersistence attendancePersistence;

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

    public void setAdministratorPersistence(AdministratorPersistence adminPersistence) {
        this.administratorPersistence = adminPersistence;
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

    //Setters for the persistence layer for each entity
    public void setQueryPersistence(QueryPersistence ap) {
        this.queryPersistence = ap;
    }
    
    public void setTeacherPersistence(TeacherPersistence ap) {
        this.teacherPersistence = ap;
    }
    
    public void setStudentSectionGradePersistence(StudentSectionGradePersistence ap) {
        this.studentSectionGradePersistence = ap;
    }
    
    public void setStudentAssignmentPersistence(StudentAssignmentPersistence ap) {
        this.studentAssignmentPersistence = ap;
    }

    public void setCoursePersistence(EntityPersistence<Course> cp) {
        coursePersistence = cp;
    }
    
    public void setSectionPersistence(SectionPersistence sectionPersistence) {
        this.sectionPersistence = sectionPersistence;
    }
    
    public void setStudentPersistence(StudentPersistence studentPersistence) {
        this.studentPersistence = studentPersistence;
    }
    
    public void setSchoolPersistence(SchoolPersistence schoolPersistence) {
        this.schoolPersistence = schoolPersistence;
    }

    public void setSchoolYearPersistence(EntityPersistence<SchoolYear> schoolYearPersistence) {
        this.schoolYearPersistence = schoolYearPersistence;
    }

    public void setTermPersistence(EntityPersistence<Term> termPersistence) {
        this.termPersistence = termPersistence;
    }

    public void setBehaviorPersistence(BehaviorPersistence behaviorPersistence) {
        this.behaviorPersistence = behaviorPersistence;
    }

    public SchoolPersistence getSchoolPersistence() {
        return schoolPersistence;
    }

    public EntityPersistence<SchoolYear> getSchoolYearPersistence() {
        return schoolYearPersistence;
    }

    public EntityPersistence<Term> getTermPersistence() {
        return termPersistence;
    }

    public StudentPersistence getStudentPersistence() {
        return studentPersistence;
    }

    public TeacherPersistence getTeacherPersistence() {
        return teacherPersistence;
    }

    public AdministratorPersistence getAdministratorPersistence() {
        return administratorPersistence;
    }

    public SectionPersistence getSectionPersistence() {
        return sectionPersistence;
    }

    public EntityPersistence<Course> getCoursePersistence() {
        return coursePersistence;
    }

    public EntityPersistence<Assignment> getAssignmentPersistence() {
        return assignmentPersistence;
    }

    public StudentAssignmentPersistence getStudentAssignmentPersistence() {
        return studentAssignmentPersistence;
    }

    public StudentSectionGradePersistence getStudentSectionGradePersistence() {
        return studentSectionGradePersistence;
    }

    public QueryPersistence getQueryPersistence() {
        return queryPersistence;
    }

     public BehaviorPersistence getBehaviorPersistence() {
         return behaviorPersistence;
     }


     public void setAssignmentPersistence(EntityPersistence<Assignment> assignmentPersistence) {
         this.assignmentPersistence = assignmentPersistence;
     }

     public UserPersistence getUserPersistence() {
         return userPersistence;
     }

     public void setUserPersistence(UserPersistence userPersistence) {
         this.userPersistence = userPersistence;
     }

     public AuthorityPersistence getAuthorityPersistence() {
         return authorityPersistence;
     }

	public void setAuthorityPersistence(AuthorityPersistence authorityPersistence) {
		this.authorityPersistence = authorityPersistence;
	}

    public SchoolDayPersistence getSchoolDayPersistence() {
        return schoolDayPersistence;
    }

    public void setSchoolDayPersistence(SchoolDayPersistence schoolDayPersistence) {
        this.schoolDayPersistence = schoolDayPersistence;
    }

    public AttendancePersistence getAttendancePersistence() {
        return attendancePersistence;
    }

    public void setAttendancePersistence(AttendancePersistence attendancePersistence) {
        this.attendancePersistence = attendancePersistence;
    }
	
}
