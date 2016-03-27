package com.scholarscore.models.user;

/**
 * The enrollment status of the student. -2=Inactive, -1=Pre-registered, 0=Currently enrolled, 1=Inactive,
 * 2=Transferred out, 3=Graduated, 4=Imported as Historical, Any other value =Inactive. Indexed.
 * Created by markroper on 3/27/16.
 */
public enum EnrollStatus {
    INACTIVE,
    PRE_REGISTERED,
    CURRENTLY_ENROLLED,
    TRANSFERRED_OUT,
    GRADUATED,
    HISTORICAL_IMPORT
}
