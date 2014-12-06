package com.scholarscore.models.factory;

import java.lang.reflect.Constructor;

import com.scholarscore.models.Assignment;

public class AssignmentFactory {
    public static Assignment cloneAssignment(Assignment assignment) {
        Assignment newAssignment = null;
        if(null != assignment) {
            @SuppressWarnings("rawtypes")
            Constructor constructor = null;
            try {
                constructor = assignment.getClass().getConstructor(assignment.getClass());
                if(null != constructor) {
                    newAssignment = (Assignment) constructor.newInstance(assignment);
                }
            } catch (Exception e) {
                //TODO: log this.  Need to setup logging. Also multi-catch support not working
                //NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationException
            }
        }
        return newAssignment;
    }
}
