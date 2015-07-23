package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.Student;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Response to deanslist 'get students' endpoint
 * 
 * Created by jwinch on 7/23/15.
 */
public class StudentResponse implements ITranslateCollection<com.scholarscore.models.Student> {
    
    Integer rowcount;
    private List<Student> data = new ArrayList<>();
    
    @Override
    public Collection<com.scholarscore.models.Student> toInternalModel() {
        // TODO: convert to scholarscore student model
        return null;
    }
    
    @Override
    public String toString() { 
        return "StudentResponse: { \"rowcount\": \"" + rowcount + "\", \"data\": [" + data.toString() + "]";
    }
}
