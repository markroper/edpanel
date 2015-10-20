package com.scholarscore.etl.powerschool.api.model;

import com.scholarscore.etl.powerschool.api.model.Term;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 7/3/15.
 */
@XmlRootElement(name = "terms")
public class Terms {
    public List<Term> term;
}
