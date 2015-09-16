package com.scholarscore.models;

/**
 * Created by cwallace on 9/16/2015.
 */
public class SectionGoal implements IGoal {
    private Integer parentId;
    private Double goalValue;
    private Integer teacherId;
    private boolean approval;
    private Integer termId;

    @Override
    public Integer getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(Integer id) {
        this.parentId = id;
    }

    @Override
    public Double getGoalValue() {
        return this.goalValue;
    }

    @Override
    public void setGoalValue(Double value) {
        this.goalValue = value;
    }

    @Override
    public Integer getTeacherId() {
        return this.teacherId;
    }

    @Override
    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public boolean getApproval() {
        return this.approval;
    }

    @Override
    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }
}
