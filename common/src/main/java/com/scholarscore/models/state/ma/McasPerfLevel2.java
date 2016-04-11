package com.scholarscore.models.state.ma;

/**
 * Created by markroper on 4/10/16.
 */
public enum McasPerfLevel2 {
    ADVANCED,
    PROFICIENT_PLUS,
    PROFICIENT,
    NEEDS_IMPROVEMENT,
    FAILING,
    WARNING;

    public static McasPerfLevel2 generate(String input) {
        switch(input) {
            case "F":
                return FAILING;
            case "W":
                return WARNING;
            case "NI":
                return NEEDS_IMPROVEMENT;
            case "P":
                return PROFICIENT;
            case "A":
                return ADVANCED;
            case "P+":
                return PROFICIENT_PLUS;
            default:
                return null;
        }
    }

}
