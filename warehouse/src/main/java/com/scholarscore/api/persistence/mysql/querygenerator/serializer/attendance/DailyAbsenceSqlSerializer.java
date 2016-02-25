package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;

/**
 * Created by markroper on 11/29/15.
 */
public class DailyAbsenceSqlSerializer extends BaseAttendanceSqlSerializer {

    @Override
    AttendanceStatus attendanceStatusMatches() { return AttendanceStatus.ABSENT; }

    @Override
    AttendanceType attendanceTypeMatches() { return AttendanceType.DAILY; }
}
