package com.scholarscore.models.state.ma;

/**
 * A=advanced(grades4-8,10),
 * P+=Above Proficient(grade 3 only),
 * P=proficient,
 * NI=needs improvement,
 * F=failing (grade 10 only),
 * W=Warning (grades 3-8),
 * INC=incomplete,
 * MED=medically documented absence,
 * LEP=limited English proficient in US school for less than a year,
 * TRN=Transferred, INP=Incomplete alt, AWR=Awareness alt,
 * EMG=Emerging alt,
 * PRG=Progressing alt,
 * NIA=Needs Improvement alt,
 * P_A=Proficient alt,
 * A_A=Advanced alt
 * Created by markroper on 4/10/16.
 */
public enum McasPerfLevel {
    ADVANCED,
    PROFICIENT_PLUS,
    PROFICIENT,
    NEEDS_IMPROVEMENT,
    FAILING,
    WARNING,
    INCOMPLETE,
    MEDICAL_ABSENCE,
    LIMITED_ENGLISH_PROFICIENT,
    TRANSFERRED,
    EMERGING_ALT,
    INCOMPLETE_ALT,
    AWARENESS_ALT,
    PROGRESSING_ALT,
    NEEDS_IMPROVEMENT_ALT,
    PROFICIENT_ALT,
    ADVANCED_ALT;

    public static McasPerfLevel generate(String input) {
        switch(input) {
            case "A":
                return ADVANCED;
            case "P+":
                return PROFICIENT_PLUS;
            case "P":
                return PROFICIENT;
            case "NI":
                return NEEDS_IMPROVEMENT;
            case "F":
                return FAILING;
            case "W":
                return WARNING;
            case "INC":
                return INCOMPLETE;
            case "MED":
                return MEDICAL_ABSENCE;
            case "LEP":
                return LIMITED_ENGLISH_PROFICIENT;
            case "TRN":
                return TRANSFERRED;
            case "EMG":
                return EMERGING_ALT;
            case "PRG":
                return PROGRESSING_ALT;
            case "NIA":
                return NEEDS_IMPROVEMENT_ALT;
            case "P_A":
                return PROFICIENT_ALT;
            case "A_A":
                return ADVANCED_ALT;
            default:
                return null;
        }
    }
}
