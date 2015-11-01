package com.scholarscore.models.attendance;

/**
 * Enumerates the supported statuses for a student's attendance
 * 
 * @author markroper
 *
 */
public enum AttendanceStatus {
    PRESENT,
    TARDY,
    EXCUSED_ABSENT,
    EXCUSED_TARDY,
    EARLY_DISMISSAL,
    EXCUSED_EARLY_DISMISSAL,
    ABSENT,
    OTHER;
}
