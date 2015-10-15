package com.scholarscore.api.persistence;

import com.scholarscore.models.goal.Goal;

import java.util.Collection;

/**
 * Created by cwallace on 9/17/2015.
 */
public interface GoalPersistence {

    public Long createGoal(Long studentId, Goal goal);

    public Goal select(long studentId, long goalId);

    public Collection<Goal> selectAll(long studentId);

    public Collection<Goal> selectAllTeacher(long teacherId);

    public Long replaceGoal(long studentId, long goalId, Goal goal);

    public Long delete(long studentId, long goalId);
}
