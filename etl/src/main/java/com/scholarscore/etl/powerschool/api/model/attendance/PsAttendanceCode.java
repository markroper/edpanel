package com.scholarscore.etl.powerschool.api.model.attendance;

import com.scholarscore.etl.IToApiModel;
import com.scholarscore.models.attendance.AttendanceStatus;

/**
 * Created by markroper on 11/1/15.
 */
public class PsAttendanceCode implements IToApiModel<AttendanceStatus> {
    public String presence_status_cd;
    public Long calculate_adm_yn;
    public String description;
    public Long calculate_ada_yn;
    public String att_code;
    public String attendancecodeinfo_guid;
    public Long yearid;
    public String unused1;
    public Double course_credit_points;
    public  Long dcid;
    public Long schoolid;
    public String sortorder;
    public Long id;
    public Long assignment_filter_yn;
    public String alternate_code;

    @Override
    public AttendanceStatus toApiModel() {
        String statusUpper = null;
        String attCodeUpper = null;
        String desc = null;
        
        if(null != presence_status_cd) {
            statusUpper = presence_status_cd.toUpperCase();
        }
        if(null != att_code) {
            attCodeUpper = att_code.toUpperCase();
        }
        if(null != description) {
            desc = description.toUpperCase();
        }

        //TODO: this is dynamically defined by each powerschool customer. we need to externalize these strings to support new clients
        switch(attCodeUpper) {
            case "":
                return AttendanceStatus.PRESENT;
            case "A-UN":
            case "UA":
                return AttendanceStatus.ABSENT;
            case "A-EX":
            case "EA":
                return AttendanceStatus.EXCUSED_ABSENT;
            case "T-UN":
            case "UT":
                return AttendanceStatus.TARDY;
            case "T-EX":
            case "ET":
                return AttendanceStatus.EXCUSED_TARDY;
            case "ED-UN":
            case "UED":
                return AttendanceStatus.EARLY_DISMISSAL;
            case "ED-EX":
            case "EED":
                return AttendanceStatus.EXCUSED_EARLY_DISMISSAL;
            default:
                break;
        }

        AttendanceStatus status = resolveFromString(statusUpper);
        if(null != status) {
            return status;
        }
        status = resolveFromString(desc);
        if(null != status) {
            return status;
        }
        return AttendanceStatus.OTHER;
    }

    private static AttendanceStatus resolveFromString(String input) {
        if(null != input) {
            if(input.contains("ABSENT") || input.contains("ABSENCE")) {
                return AttendanceStatus.ABSENT;
            }
            if(input.contains("PRESENT")) {
                return AttendanceStatus.PRESENT;
            }
            if(input.contains("TARDY")) {
                return AttendanceStatus.TARDY;
            }
            if(input.contains("DISMISSAL")) {
                return AttendanceStatus.EARLY_DISMISSAL;
            }
        }
        return null;
    }
}
