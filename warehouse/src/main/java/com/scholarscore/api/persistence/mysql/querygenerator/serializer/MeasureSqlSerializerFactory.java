package com.scholarscore.api.persistence.mysql.querygenerator.serializer;

import com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic.AssignmentGradeSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic.CourseGradeSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic.CurrentGpaSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic.GpaSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.academic.HomeworkCompletionSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.AttendanceSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.DailyAbsenceSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.DailyTardySqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.SectionAbsenceSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance.SectionTardySqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior.DemeritSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior.MeritSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.behavior.ReferralSqlSerializer;
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
            default:
                return null;
        }
    }
}
