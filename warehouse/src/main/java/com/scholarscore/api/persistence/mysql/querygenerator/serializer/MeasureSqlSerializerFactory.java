package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.models.query.Measure;

public class MeasureSqlSerializerFactory {
    public static MeasureSqlSerializer get(Measure measure) {
        switch (measure) {
            case ASSIGNMENT_GRADE:
                return new AssignmentGradeSqlSerializer();
            case COURSE_GRADE:
                return new CourseGradeSqlSerializer();
            case HW_COMPLETION:
                return new HomeworkCompletionSqlSerializer();
            case SECTION_ABSENCE:
                return new SectionAbsenceSqlSerializer();
            case SECTION_TARDY:
                return new SectionTardySqlSerializer();
            case ATTENDANCE:
                return new AttendanceSqlSerializer();
            case ABSENCE:
                return new DailyAbsenceSqlSerializer();
            case TARDY:
                return new DailyTardySqlSerializer();
            case DEMERIT:
                return new DemeritSqlSerializer();
            case MERIT:
                return new MeritSqlSerializer();
            case REFERRAL:
                return new ReferralSqlSerializer();
            case GPA:
                return new GpaSqlSerializer();
            case CURRENT_GPA:
                return new CurrentGpaSqlSerializer();
            case DETENTION:
                return new DetentionSqlSerializer();
            case IN_SCHOOL_SUSPENSION:
                return new InSchoolSuspensionSqlSerializer();
            case OUT_OF_SCHOOL_SUSPENSION:
                return new OutOfSchoolSuspensionSqlSerializer();
            default:
                return null;
        }
    }
}
