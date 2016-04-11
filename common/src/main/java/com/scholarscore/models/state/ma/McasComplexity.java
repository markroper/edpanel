package com.scholarscore.models.state.ma;

/**
 * ALT=Alternate Achievement Standards, MOD=Modified Achievement Standards, GL=Grade Level Achievement Standards)

 * Created by markroper on 4/10/16.
 */
public enum McasComplexity {
    ALT,
    MOD,
    GL;

    public static McasComplexity generate(String input) {
        switch(input) {
            case "ALT":
                return ALT;
            case "MOD":
                return MOD;
            default:
                return GL;
        }
    }
}
