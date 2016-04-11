package com.scholarscore.models.state.ma;

/**
 * Created by markroper on 4/10/16.
 */
public enum McasStatus {
    TESTED,
    NOT_TESTED_ABSENT,
    NOT_TESTED_MEDICAL,
    NOT_TESTED_OTHER;

    public static McasStatus generate(String input) {
        switch(input) {
            case "T":
                return TESTED;
            case "NTA":
                return NOT_TESTED_ABSENT;
            case "NTM":
                return NOT_TESTED_MEDICAL;
            case "NTO":
                return NOT_TESTED_OTHER;
            default:
                return null;
        }
    }
}
