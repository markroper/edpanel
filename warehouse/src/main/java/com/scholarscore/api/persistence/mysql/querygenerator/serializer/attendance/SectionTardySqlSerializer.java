package com.scholarscore.api.persistence.mysql.querygenerator.serializer.attendance;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;

/**
 * Created by markroper on 11/29/15.
 */
public class SectionTardySqlSerializer extends AttendanceSqlSerializer {

    @Override
    AttendanceStatus attendanceStatusMatches() { return AttendanceStatus.TARDY; }

    @Override
    AttendanceTypes attendanceTypeMatches() { return AttendanceTypes.SECTION; }
}
