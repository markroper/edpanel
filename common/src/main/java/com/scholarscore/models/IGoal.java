package com.scholarscore.models;

import javax.persistence.Transient;

/**
 * Created by cwallace on 9/20/2015.
 */
public interface IGoal {

    @Transient
    public Double getCalculatedValue();



}
