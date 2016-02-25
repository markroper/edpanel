package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;

/**
 * Created by markroper on 11/29/15.
 */
public class SectionTardySqlSerializer extends BaseAttendanceSqlSerializer {

    @Override
    AttendanceStatus attendanceStatusMatches() { return AttendanceStatus.TARDY; }

    @Override
    AttendanceType attendanceTypeMatches() { return AttendanceType.SECTION; }
}
