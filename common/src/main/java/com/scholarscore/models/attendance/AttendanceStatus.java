package com.scholarscore.models.attendance;

/**
 * Enumerates the supported statuses for a student's attendance
 * 
 * @author markroper
 *
 */
public enum AttendanceStatus {
    PRESENT,
    MORNING_TARDY,
    MORNING_TARDY_ABSENT,
    TARDY,
    AFTERNOON_TARDY,
    AFTERNOON_TARDY_ABSENT,
    EXCUSED_ABSENT,
    EXCUSED_TARDY,
    EARLY_DISMISSAL,
    EXCUSED_EARLY_DISMISSAL,
    ABSENT,
    INTERNAL_SUSPENSION,
    LEAVE_OF_ABSENCE,
    OTHER;
}
