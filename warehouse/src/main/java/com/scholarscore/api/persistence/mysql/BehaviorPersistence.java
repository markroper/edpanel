package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Behavior;

import java.util.Collection;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 6:17 PM
 */
public interface BehaviorPersistence {

    public Collection<Behavior> selectAll();
    
    public Behavior select(long behaviorId);
    
    public Long createBehavior(Behavior behavior);
    
    public Long replaceBehavior(long behaviorId, Behavior behavior);
    
    public Long delete(long behaviorId);
    
    /* 
    * 
    *   public Collection<Student> selectAll();
    
    public Collection<Student> selectAllStudentsInSection(long sectionId);

    public Student select(long studentId);

    public Long createStudent(Student student);

    public Long replaceStudent(long studentId, Student student);

    public Long delete(long studentId);
    * 
    * * * * * */

}
