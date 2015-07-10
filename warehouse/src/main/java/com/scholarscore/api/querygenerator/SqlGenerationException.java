package com.scholarscore.api.querygenerator;

public class SqlGenerationException extends Exception {
    private static final long serialVersionUID = 1L;

    public SqlGenerationException(String msg){
        super(msg);
    }
}
