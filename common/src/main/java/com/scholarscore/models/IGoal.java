package com.scholarscore.models;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by cwallace on 9/20/2015.
 */
public interface IGoal {

    @Transient
    public Float getCalculatedValue();



}
