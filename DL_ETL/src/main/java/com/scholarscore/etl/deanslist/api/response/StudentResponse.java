package com.scholarscore.etl.deanslist.api.response;

import com.scholarscore.etl.deanslist.api.model.Student;
import com.scholarscore.etl.powerschool.api.response.ITranslate;
import com.scholarscore.etl.powerschool.api.response.ITranslateCollection;

import java.util.Collection;

/**
 * Created by jwinch on 7/23/15.
 */
public class StudentResponse implements ITranslateCollection<com.scholarscore.models.Student> {
    
    public Student student;

    @Override
    public Collection<com.scholarscore.models.Student> toInternalModel() {
//        return null;
    }
}
