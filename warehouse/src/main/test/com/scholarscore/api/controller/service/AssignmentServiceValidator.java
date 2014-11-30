package com.scholarscore.api.controller.service;

import org.springframework.http.HttpStatus;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Assignment;

public class AssignmentServiceValidator {

    private final IntegrationBase sb;
    
    public AssignmentServiceValidator(IntegrationBase sb) {
        this.sb = sb;
    }
    
    //Create an assignment (POST)
    public Assignment create(Assignment assignment, String msg) {
        return null;
    }
    
    public void createNegative(Assignment assignment, HttpStatus expectedCode, String msg) {
        
    }
    
    //Replace an assignment (PUT)
    public Assignment replace(Assignment assignment, String msg) {
        return null;
    }
    
    public void replaceNegative(Assignment assignment, HttpStatus expectedCode, String msg) {
        
    }
    
    //Update an assignment (PATCH)
    public Assignment update(Assignment assignment, String msg) {
        return null;
    }
    
    public void updateNegative(Assignment assignment, HttpStatus expectedCode, String msg) {
        
    }
    
    //Delete an assignment (DELETE)
    public void delete(Long assignment, String msg) {
        
    }
    
    public void deleteNegative(Long assignmentId, HttpStatus expectedCode, String msg) {
        
    }
}
