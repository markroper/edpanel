package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 7/3/15.
 */
@XmlRootElement(name = "sections")
public class PsSections {
    public List<PsSection> section;
}
