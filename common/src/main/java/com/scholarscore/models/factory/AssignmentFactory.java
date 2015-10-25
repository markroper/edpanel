package com.scholarscore.models.factory;

import com.scholarscore.models.assignment.Assignment;

import java.lang.reflect.Constructor;

/**
 * Given an instance of a subclass of Assignment, produces a shallow clone of that 
 * instance.
 * 
 * @author markroper
 * @see Assignment
 * @see GradedAssignment
 * @see AttendanceAssignment
 *
 */
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
