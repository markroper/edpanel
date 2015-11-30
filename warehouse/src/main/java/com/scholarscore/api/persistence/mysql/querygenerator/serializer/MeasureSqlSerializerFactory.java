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
            default:
                return null;
        }
    }
}
