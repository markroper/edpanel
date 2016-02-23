package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;

/**
 * Created by cwallace on 1/4/16.
 */
public class SectionAbsenceSqlSerializer extends BaseAttendanceSqlSerializer {

    @Override
    AttendanceStatus attendanceStatusMatches() { return AttendanceStatus.ABSENT; }

    @Override
    AttendanceTypes attendanceTypeMatches() { return AttendanceTypes.SECTION; }
}
