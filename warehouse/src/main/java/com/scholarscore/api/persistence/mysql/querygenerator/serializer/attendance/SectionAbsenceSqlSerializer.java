package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;

/**
 * Created by cwallace on 1/4/16.
 */
public class SectionAbsenceSqlSerializer extends BaseAttendanceSqlSerializer {

    @Override
    AttendanceStatus attendanceStatusMatches() { return AttendanceStatus.ABSENT; }

    @Override
    AttendanceType attendanceTypeMatches() { return AttendanceType.SECTION; }
}
