package com.scholarscore.models;

import java.io.Serializable;

/**
 * Created by cwallace on 9/16/2015.
 */
public interface IGoal extends Serializable {

    public Integer getParentId();

    public void setParentId(Integer id);

    public Double getGoalValue();

    public void setGoalValue(Double value);

    public Integer getTeacherId();

    public void setTeacherId(Integer teacherId);

    public boolean getApproval();

    public void setApproval(boolean approval);






}
