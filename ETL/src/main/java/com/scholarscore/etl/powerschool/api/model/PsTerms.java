package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 7/3/15.
 */
@XmlRootElement(name = "terms")
public class PsTerms {
    public List<PsTerm> term;
}
